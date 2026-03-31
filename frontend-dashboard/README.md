# Frontend Dashboard

Standalone React + TypeScript dashboard for the Incident Intelligence platform.

## Setup

```bash
cd frontend-dashboard
cp .env.example .env
npm install
npm start
```

The app runs independently and expects backend APIs to be available at:

`REACT_APP_API_BASE_URL` (configured in `.env`)

## Implemented Features

- Service overview dashboard (`GET /api/services`)
- Service details logs view (`GET /api/services/:id/logs`)
- AI analysis panel (`POST /api/ai/analyze`)
- Tailwind-based responsive UI
- Loading and error states
- Search filter and 5-second polling on dashboard
