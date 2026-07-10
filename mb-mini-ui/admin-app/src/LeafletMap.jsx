import React, { useEffect, useRef } from 'react';

// Renders a set of [lat, lng] points either as a route (polyline + numbered
// markers) or as a closed polygon. Degrades gracefully if Leaflet (loaded via
// CDN in index.html) is unavailable.
export default function LeafletMap({ points = [], mode = 'route' }) {
  const elRef = useRef(null);
  const mapRef = useRef(null);
  const layerRef = useRef(null);

  useEffect(() => {
    const L = window.L;
    if (!L || !elRef.current) return;
    if (!mapRef.current) {
      mapRef.current = L.map(elRef.current).setView([28.4083, 77.0682], 12);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
        maxZoom: 19,
      }).addTo(mapRef.current);
    }
    const map = mapRef.current;
    if (layerRef.current) { map.removeLayer(layerRef.current); layerRef.current = null; }

    const valid = points.filter((p) => Array.isArray(p) && p.length >= 2 && p.every((n) => typeof n === 'number' && !Number.isNaN(n)));
    if (valid.length === 0) return;

    const group = L.layerGroup().addTo(map);
    if (mode === 'polygon') {
      L.polygon(valid, { color: '#4f46e5', fillOpacity: 0.15 }).addTo(group);
    } else {
      L.polyline(valid, { color: '#4f46e5', weight: 3 }).addTo(group);
    }
    valid.forEach((p, i) => {
      L.circleMarker(p, { radius: 7, color: '#3730a3', fillColor: '#4f46e5', fillOpacity: 1 })
        .bindTooltip(String(i + 1), { permanent: true, direction: 'center', className: 'map-idx' })
        .addTo(group);
    });
    layerRef.current = group;
    try { map.fitBounds(L.latLngBounds(valid).pad(0.2)); } catch { /* single point */ }
  }, [points, mode]);

  if (!window.L) {
    return (
      <div className="notice" style={{ margin: 0 }}>
        Map library not loaded (needs internet access for the Leaflet CDN). The coordinate list below still works.
      </div>
    );
  }
  return <div className="map" ref={elRef} />;
}
