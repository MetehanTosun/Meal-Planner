# Meal Planner for SoftwarePraktikum team-5 AEB

A simple Meal Planner Web Application, where you can plan your meals for the week and automatically generate Shopping lists.

You can visit the application via [http://193.196.53.132/](http://193.196.53.132)

## Requirements

- Docker & Docker Compose

## Setup and Run

### 1. Clone the repository
```bash
git clone <repository-url>
cd <repository-folder>
```

### 2. Create a `.env` file in the project root
```
MARIADB_PW=yourpassword
```

### 3. Start all services
```bash
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080

---

## Run without Docker

### Requirements
- Java 21
- Maven
- Node.js

### Database
Start only the database via Docker:
```bash
docker compose up db -d
```

### Backend
```bash
cd backend/backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```


## Help



## Authors

Mika Walthaner  
st188053@dstud.uni-stuttgart.de

Ethan Banovic
st188321@stud.uni-stuttgart.de

Metehan Tosun
st188557@stud.uni-stuttgart.de



## Version History



## License

