// https://gist.github.com/gomezcabo/dff1d95fd1eb354f686d6606a511d7da
export type RecursiveRequired<T> = Required<{
  [P in keyof T]: T[P] extends object | undefined ? RecursiveRequired<Required<T[P]>> : T[P]
}>

export const formatPrice = (price: number) => {
  return new Intl.NumberFormat('pl-PL', {
    style: 'currency',
    currency: 'PLN',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(price)
}
