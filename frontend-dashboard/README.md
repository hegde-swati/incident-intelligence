# Frontend Dashboard

Standalone React + TypeScript dashboard for Incident Intelligence.

## Run locally

1. Copy env template:
   ```bash
   cp .env.example .env
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the app:
   ```bash
   npm start
   ```

## Build for production

```bash
npm run build
```

The app runs independently and expects backend APIs at `VITE_API_BASE_URL`.
