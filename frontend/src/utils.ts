// https://gist.github.com/gomezcabo/dff1d95fd1eb354f686d6606a511d7da
export type RecursiveRequired<T> = Required<{
  [P in keyof T]: T[P] extends object | undefined ? RecursiveRequired<Required<T[P]>> : T[P]
}>

export const formatPrice = (price: number) => {
  return new Intl.NumberFormat('pl-PL', {
    style: 'currency',
    currency: 'PLN',
    minimumFractionDigits: price % 1 === 0 ? 0 : 2,
    maximumFractionDigits: price % 1 === 0 ? 0 : 2,
  }).format(price)
}

export const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pl-PL')
}

export const getRoomsLabel = (count: number) => {
  if (count === 1) return 'pokój'
  if (count >= 2 && count <= 4) return 'pokoje'
  return 'pokoi'
}

export const getPropertiesLabel = (count: number) => {
  return count === 1 ? 'nieruchomość' : 'nieruchomości'
}
