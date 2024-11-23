# Meal Planner for SoftwarePraktikum team-5 AEB

A simple Meal Planner Web Application, where you can plan your meals for the week and automatically generate Shopping lists.

## Requirements

## Setup and Run

To run the project yourself:

### 1. Clone the repository
```bash
git clone <repository-url>
cd <repository-folder>
```
## Frontend
 The frontend for the web application is in the /frintend directory
## Backend

The backend is in the /backend/backend directory

### 2. Build the project
```bash
mvn clean install
```
### 3. Configure the API Key

The application requires a Gustar.io API key to interact with the extern API. Set it as an environment variable:

Go to [https://rapidapi.com/gustario-gustario-default/api/gustar-io-deutsche-rezepte](https://rapidapi.com/gustario-gustario-default/api/gustar-io-deutsche-rezepte)

Create a free Account and retrieve your personal API Key


#### On Linux/Mac:
```bash
export GUSTAR_API_KEY=<your-api-key>
```

#### On Windows (PowerShell):
```powershell
$env:GUSTAR_API_KEY="<your-api-key>"
```

**Important**: Make sure the variable `GUSTAR_API_KEY` matches the name used in `application.properties`:

```properties
spring.sendgrid.api-key=${GUSTAR_API_KEY}
```

### 4. Run the application

Run the project with Maven:
```bash
mvn spring-boot:run
```

Alternatively, run the generated JAR file:
```bash
java -jar target/<project-name>.jar
```

---

### Dependencies



### Installing



### Executing program


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

