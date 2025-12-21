import './globals.css'

export const metadata = {
  title: 'Chess',
  description: 'Chess Engine',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  )
}
