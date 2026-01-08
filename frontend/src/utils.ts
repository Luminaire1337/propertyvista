// https://gist.github.com/gomezcabo/dff1d95fd1eb354f686d6606a511d7da
export type RecursiveRequired<T> = Required<{
  [P in keyof T]: T[P] extends object | undefined ? RecursiveRequired<Required<T[P]>> : T[P]
}>

export const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('pl-PL', {
    style: 'currency',
    currency: 'PLN',
    minimumFractionDigits: price % 1 === 0 ? 0 : 2,
    maximumFractionDigits: price % 1 === 0 ? 0 : 2,
  }).format(price)
}

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('pl-PL')
}

export const getRoomsLabel = (count: number): string => {
  if (count === 1) return 'pokój'
  if (count >= 2 && count <= 4) return 'pokoje'
  return 'pokoi'
}

export const getPropertiesLabel = (count: number): string => {
  return count === 1 ? 'ogłoszenie' : 'ogłoszeń'
}

export const urlToFile = async (url: string): Promise<File> => {
  const response = await fetch(url)
  const blob = await response.blob()

  // Get last segment of URL as filename
  const segments = url.split('/')
  const fileName = segments[segments.length - 1] || 'file'

  // Get content type from headers
  const contentType = response.headers.get('Content-Type') || ''

  return new File([blob], fileName, { type: contentType })
}

export const checkDifferenceBetweenArrays = <T>(arr1: T[], arr2: T[]): boolean => {
  if (arr1.length !== arr2.length) return true
  const set1 = new Set(arr1)
  const set2 = new Set(arr2)
  for (const item of set1) {
    if (!set2.has(item)) return true
  }
  return false
}
