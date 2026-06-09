import { useCallback, useEffect, useState } from 'react'
import {
  createCatalog,
  fetchCatalogs,
  refreshCache,
  updateCatalog,
} from './api/client'
import { CatalogForm } from './components/CatalogForm'
import { CatalogTable } from './components/CatalogTable'
import { Modal } from './components/Modal'
import { StatsCards } from './components/StatsCards'
import { Toast } from './components/Toast'
import type { Catalog, CatalogCreate } from './types/catalog'

type ModalMode = 'create' | 'edit' | null

function App() {
  const [catalogs, setCatalogs] = useState<Catalog[]>([])
  const [loading, setLoading] = useState(true)
  const [modalMode, setModalMode] = useState<ModalMode>(null)
  const [editingCatalog, setEditingCatalog] = useState<Catalog | undefined>()
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [refreshing, setRefreshing] = useState(false)

  const showToast = useCallback((message: string, type: 'success' | 'error' = 'success') => {
    setToast({ message, type })
    setTimeout(() => setToast(null), 4000)
  }, [])

  const loadCatalogs = useCallback(async () => {
    setLoading(true)
    try {
      const data = await fetchCatalogs()
      setCatalogs(data)
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Failed to load catalog', 'error')
    } finally {
      setLoading(false)
    }
  }, [showToast])

  useEffect(() => {
    loadCatalogs()
  }, [loadCatalogs])

  function openCreate() {
    setEditingCatalog(undefined)
    setModalMode('create')
  }

  function openEdit(catalog: Catalog) {
    setEditingCatalog(catalog)
    setModalMode('edit')
  }

  function closeModal() {
    setModalMode(null)
    setEditingCatalog(undefined)
  }

  async function handleCreate(data: CatalogCreate) {
    const message = await createCatalog(data)
    showToast(message)
    closeModal()
    await loadCatalogs()
  }

  async function handleUpdate(data: { id: number } & Partial<CatalogCreate>) {
    const message = await updateCatalog(data)
    showToast(message)
    closeModal()
    await loadCatalogs()
  }

  async function handleCacheRefresh() {
    setRefreshing(true)
    try {
      const message = await refreshCache()
      showToast(message)
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Cache refresh failed', 'error')
    } finally {
      setRefreshing(false)
    }
  }

  return (
    <div className="min-h-screen bg-[#0f1117]">
      <header className="border-b border-white/10 bg-[#0f1117]/80 backdrop-blur-md">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <div>
            <h1 className="text-xl font-bold tracking-tight text-white">Catalog Dashboard</h1>
            <p className="mt-0.5 text-sm text-slate-500">mb-mini API · MySQL + Redis</p>
          </div>
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={handleCacheRefresh}
              disabled={refreshing}
              className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/5 disabled:opacity-50"
            >
              {refreshing ? 'Refreshing…' : 'Refresh Cache'}
            </button>
            <button
              type="button"
              onClick={loadCatalogs}
              className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/5"
            >
              Reload
            </button>
            <button
              type="button"
              onClick={openCreate}
              className="rounded-lg bg-sky-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-sky-500"
            >
              + Add Product
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl space-y-8 px-6 py-8">
        <StatsCards catalogs={catalogs} />

        <section>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold text-white">Product Catalog</h2>
            <span className="text-sm text-slate-500">{catalogs.length} items</span>
          </div>
          <CatalogTable catalogs={catalogs} onEdit={openEdit} loading={loading} />
        </section>
      </main>

      <Modal
        title={modalMode === 'create' ? 'Add Product' : 'Edit Product'}
        open={modalMode !== null}
        onClose={closeModal}
      >
        <CatalogForm
          mode={modalMode === 'create' ? 'create' : 'edit'}
          initial={editingCatalog}
          onSubmit={async (data) => {
            if (modalMode === 'create') {
              await handleCreate(data)
            } else if (editingCatalog) {
              await handleUpdate({ id: editingCatalog.id, ...data })
            }
          }}
          onCancel={closeModal}
        />
      </Modal>

      <Toast
        message={toast?.message ?? null}
        type={toast?.type}
        onDismiss={() => setToast(null)}
      />
    </div>
  )
}

export default App
