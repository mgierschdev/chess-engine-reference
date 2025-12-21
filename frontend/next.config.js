/** @type {import('next').NextConfig} */
const nextConfig = {
  // Disable font optimization to prevent build failures in CI
  optimizeFonts: false,
}

module.exports = nextConfig
