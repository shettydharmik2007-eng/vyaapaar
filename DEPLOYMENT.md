# 🚀 Vyaapaar Deployment Guide

This project is configured with zero-dependency containerization, Docker multi-stage builds, environment variable port mapping, and automatic database fallback.

---

## ⚡ Option 1: Free Cloud Deployment on Render (Recommended)

Render offers free hosting for Docker web services.

1. **Push your code to GitHub**:
   ```bash
   git add .
   git commit -m "Add Docker and cloud deployment configuration"
   git push origin main
   ```
2. **Go to [render.com](https://render.com/)** and sign in with GitHub.
3. Click **New +** -> **Web Service**.
4. Select your **`Vyaapaar`** repository.
5. Set:
   - **Environment**: `Docker`
   - **Plan**: `Free`
6. Click **Create Web Service**.
7. Render will automatically build the Docker container and provide a live HTTPS URL (e.g. `https://vyaapaar-app.onrender.com`).

---

## ⚡ Option 2: 1-Click Cloud Deployment on Railway

1. **Go to [railway.app](https://railway.app/)** and sign in.
2. Click **New Project** -> **Deploy from GitHub repo**.
3. Select your repository.
4. Railway will automatically detect the `Dockerfile` / `Procfile`, build, and assign a live public domain.

---

## ⚡ Option 3: Local Network / Phone Access

### Local Wi-Fi Access:
1. Make sure your phone and laptop are on the same Wi-Fi.
2. Open on phone: `http://192.168.0.101:8080/`

### Instant Public Tunnel (Access from anywhere via mobile data):
Run the following in your terminal:
```bash
npx localtunnel --port 8080
```
This gives you an instant public HTTPS link (e.g., `https://example-subdomain.loca.lt`) without needing to set up any cloud accounts.

---

## 🐳 Option 4: Local Docker Container

To build and run locally with Docker:
```bash
# 1. Build Docker image
docker build -t vyaapaar-web .

# 2. Run container
docker run -p 8080:8080 vyaapaar-web
```
Access at: `http://localhost:8080/`

---

## 🗄️ Database Configuration in Cloud

By default, the server runs with built-in zero-config relational storage (in-memory MySQL mode). If you want to connect a live MySQL or PostgreSQL cloud database (e.g. from Aiven, Supabase, PlanetScale, or Railway MySQL), set these environment variables:

| Environment Variable | Description |
| :--- | :--- |
| `PORT` | Web server port (Default: `8080`, managed automatically by Render/Railway) |
| `DB_URL` | JDBC MySQL connection URL (`jdbc:mysql://<host>:<port>/<dbname>?useSSL=true`) |
| `DB_USER` | MySQL Username |
| `DB_PASSWORD` | MySQL Password |
