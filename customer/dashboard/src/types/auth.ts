export interface AuthSession {
  authKey: string
  refreshToken: string
  customerId: number
  phone: string
  name?: string
  email?: string
}

export interface RegisterPayload {
  Name: string
  PhoneNo: string
  Password: string
  Email: string
  HouseNo?: number | null
  Locality?: string | null
  City?: string | null
  Pincode?: number | null
}

export interface CustomerProfile {
  id: number
  name: string
  phoneNo: string
  email: string
}
