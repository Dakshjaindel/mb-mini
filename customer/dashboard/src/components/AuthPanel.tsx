import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import type { RegisterPayload } from '../types/auth'

type Step = 'phone' | 'login' | 'register'

interface AuthPanelProps {
  onSuccess: () => void
  onError: (message: string) => void
}

export function AuthPanel({ onSuccess, onError }: AuthPanelProps) {
  const { checkPhone, login, register } = useAuth()
  const [step, setStep] = useState<Step>('phone')
  const [phone, setPhone] = useState('')
  const [loading, setLoading] = useState(false)

  async function handlePhoneSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!/^[0-9]{10}$/.test(phone)) {
      onError('Phone number must be exactly 10 digits.')
      return
    }
    setLoading(true)
    try {
      const next = await checkPhone(phone)
      setStep(next)
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Could not verify phone number')
    } finally {
      setLoading(false)
    }
  }

  async function handleLogin(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const password = (e.currentTarget.elements.namedItem('password') as HTMLInputElement).value
    setLoading(true)
    try {
      await login(phone, password)
      onSuccess()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  async function handleRegister(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    const houseVal = (form.elements.namedItem('house') as HTMLInputElement).value
    const pincodeVal = (form.elements.namedItem('pincode') as HTMLInputElement).value

    const payload: RegisterPayload = {
      Name: (form.elements.namedItem('name') as HTMLInputElement).value.trim(),
      PhoneNo: phone,
      Password: (form.elements.namedItem('password') as HTMLInputElement).value,
      Email: (form.elements.namedItem('email') as HTMLInputElement).value.trim(),
      HouseNo: houseVal ? Number(houseVal) : null,
      Locality: (form.elements.namedItem('locality') as HTMLInputElement).value.trim() || null,
      City: (form.elements.namedItem('city') as HTMLInputElement).value.trim() || null,
      Pincode: pincodeVal ? Number(pincodeVal) : null,
    }

    setLoading(true)
    try {
      await register(payload)
      onSuccess()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto grid max-w-4xl gap-8 lg:grid-cols-[280px_1fr]">
      <aside className="rounded-2xl border border-white/10 bg-[#121722] p-6">
        <p className="text-xs font-semibold uppercase tracking-wider text-sky-400">Account</p>
        <h2 className="mt-2 text-2xl font-bold text-white">Welcome back</h2>
        <p className="mt-2 text-sm text-slate-400">
          Sign in or create an account to browse the catalog, manage your basket, and place orders.
        </p>
        <ol className="mt-8 space-y-4 text-sm">
          <StepItem done={step !== 'phone'} active={step === 'phone'} label="Phone number" />
          <StepItem
            done={false}
            active={step === 'login' || step === 'register'}
            label={step === 'register' ? 'Register' : 'Login'}
          />
        </ol>
      </aside>

      <section className="rounded-2xl border border-white/10 bg-[#121722] p-8">
        {step === 'phone' && (
          <form onSubmit={handlePhoneSubmit} className="space-y-5">
            <div>
              <h3 className="text-xl font-semibold text-white">Enter your phone</h3>
              <p className="mt-1 text-sm text-slate-400">We&apos;ll check if you already have an account.</p>
            </div>
            <label className="block">
              <span className="mb-2 block text-sm text-slate-400">Phone number</span>
              <div className="flex overflow-hidden rounded-xl border border-white/10 bg-[#0b0d12]">
                <span className="flex items-center px-4 text-sm text-slate-500">+91</span>
                <input
                  type="tel"
                  inputMode="numeric"
                  maxLength={10}
                  value={phone}
                  onChange={(e) => setPhone(e.target.value.replace(/\D/g, '').slice(0, 10))}
                  placeholder="9876543210"
                  className="w-full bg-transparent py-3 pr-4 text-white outline-none"
                  required
                />
              </div>
            </label>
            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-xl bg-sky-600 py-3 text-sm font-semibold text-white transition hover:bg-sky-500 disabled:opacity-50"
            >
              {loading ? 'Checking…' : 'Continue'}
            </button>
          </form>
        )}

        {step === 'login' && (
          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <h3 className="text-xl font-semibold text-white">Sign in</h3>
              <p className="mt-1 text-sm text-slate-400">
                Account found for <span className="text-white">+91 {phone}</span>
              </p>
            </div>
            <label className="block">
              <span className="mb-2 block text-sm text-slate-400">Password</span>
              <input
                name="password"
                type="password"
                required
                className="w-full rounded-xl border border-white/10 bg-[#0b0d12] px-4 py-3 text-white outline-none focus:border-sky-500/50"
              />
            </label>
            <div className="flex gap-3">
              <button
                type="button"
                onClick={() => setStep('phone')}
                className="rounded-xl border border-white/10 px-4 py-3 text-sm text-slate-300 hover:bg-white/5"
              >
                Back
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 rounded-xl bg-sky-600 py-3 text-sm font-semibold text-white hover:bg-sky-500 disabled:opacity-50"
              >
                {loading ? 'Signing in…' : 'Sign in'}
              </button>
            </div>
          </form>
        )}

        {step === 'register' && (
          <form onSubmit={handleRegister} className="space-y-4">
            <div>
              <h3 className="text-xl font-semibold text-white">Create account</h3>
              <p className="mt-1 text-sm text-slate-400">
                New customer for <span className="text-white">+91 {phone}</span>
              </p>
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <Field name="name" label="Full name" required />
              <Field name="email" label="Email" type="email" required />
              <Field name="password" label="Password" type="password" required />
              <Field name="house" label="House no." type="number" />
              <Field name="locality" label="Locality" />
              <Field name="city" label="City" />
              <Field name="pincode" label="Pincode" type="number" />
            </div>
            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => setStep('phone')}
                className="rounded-xl border border-white/10 px-4 py-3 text-sm text-slate-300 hover:bg-white/5"
              >
                Back
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 rounded-xl bg-emerald-600 py-3 text-sm font-semibold text-white hover:bg-emerald-500 disabled:opacity-50"
              >
                {loading ? 'Creating…' : 'Register & continue'}
              </button>
            </div>
          </form>
        )}
      </section>
    </div>
  )
}

function StepItem({ done, active, label }: { done: boolean; active: boolean; label: string }) {
  return (
    <li className="flex items-center gap-3">
      <span
        className={`flex h-7 w-7 items-center justify-center rounded-full text-xs font-bold ${
          done
            ? 'bg-emerald-500/20 text-emerald-400'
            : active
              ? 'bg-sky-500/20 text-sky-400'
              : 'bg-white/5 text-slate-500'
        }`}
      >
        {done ? '✓' : '•'}
      </span>
      <span className={active ? 'text-white' : 'text-slate-500'}>{label}</span>
    </li>
  )
}

function Field({
  name,
  label,
  type = 'text',
  required,
}: {
  name: string
  label: string
  type?: string
  required?: boolean
}) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs text-slate-500">{label}</span>
      <input
        name={name}
        type={type}
        required={required}
        className="w-full rounded-xl border border-white/10 bg-[#0b0d12] px-3 py-2.5 text-sm text-white outline-none focus:border-sky-500/50"
      />
    </label>
  )
}
