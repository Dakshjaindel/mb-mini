interface ToastProps {
  message: string | null
  type?: 'success' | 'error'
  onDismiss: () => void
}

export function Toast({ message, type = 'success', onDismiss }: ToastProps) {
  if (!message) return null

  return (
    <div className="fixed bottom-6 right-6 z-50 flex max-w-sm items-start gap-3 rounded-xl border border-white/10 bg-[#1a2030] px-4 py-3 shadow-xl">
      <span
        className={`mt-0.5 h-2 w-2 shrink-0 rounded-full ${
          type === 'success' ? 'bg-emerald-400' : 'bg-red-400'
        }`}
      />
      <p className="flex-1 text-sm text-slate-200">{message}</p>
      <button
        type="button"
        onClick={onDismiss}
        className="text-slate-500 transition hover:text-white"
      >
        ×
      </button>
    </div>
  )
}
