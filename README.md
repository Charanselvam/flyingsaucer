# PDF Invoice Generation System

A Spring Boot application for generating professional PDF invoices using HTML templates with Flying Saucer and iText.

## 📋 Overview

This project provides a RESTful API for generating PDF invoices from both JSON input and database records. It uses Thymeleaf templates for HTML rendering and Flying Saucer with iText for PDF conversion.

## ✨ Features

- **PDF Generation**: Convert HTML templates to PDF documents
- **Multiple Data Sources**: Generate invoices from JSON input or database records
- **Professional Templates**: Clean, professional invoice templates with logo support
- **RESTful API**: Easy-to-use endpoints for all operations
- **Swagger Documentation**: Interactive API documentation
- **Database Integration**: PostgreSQL support for storing invoice data

## 🛠️ Technologies Used

- **Spring Boot 3.5.5** - Application framework
- **Thymeleaf** - HTML template engine
- **Flying Saucer + iText5** - PDF generation
- **PostgreSQL** - Database
- **Spring Data JPA** - Database access
- **Springdoc OpenAPI** - API documentation
- **Java 21** - Programming language

## 📦 Dependencies

Key dependencies include:
- `spring-boot-starter-web` - Web MVC support
- `spring-boot-starter-thymeleaf` - Template engine
- `spring-boot-starter-data-jpa` - Database access
- `flying-saucer-pdf-itext5` - PDF generation
- `springdoc-openapi-starter-webmvc-ui` - API documentation
- `postgresql` - Database driver
- `spring-boot-starter-validation` - Input validation

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL database

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd flyingsaucer
   ```

2. **Configure database**
   Update `application.properties` with your PostgreSQL credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/invoice_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## 📚 API Documentation

Once the application is running, access the API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📋 API Endpoints

### Generate PDF from JSON
```http
POST /api/pdf/generate
Content-Type: application/json

{
  "invoiceId": "INV-2024-001",
  "customerName": "John Doe",
  "date": "2024-01-15",
  "items": [
    {
      "name": "Product A",
      "quantity": 2,
      "price": 100.00
    }
  ],
  "totalAmount": 200.00
}
```

### Generate PDF from Database
```http
GET /api/pdf/invoice/{id}
```

### Save Invoice to Database
```http
POST /api/pdf/save
Content-Type: application/json
```

### Generate PDF with Download Link
```http
POST /api/pdf/generate-with-link
Content-Type: application/json
```

## 🎨 Customization

### Template Modification
Edit the invoice template at:
```
src/main/resources/templates/invoice.html
```

### Adding a Logo
Place your logo image at:
```
src/main/resources/static/images/logo.png
```

The system automatically converts the logo to base64 and embeds it in the PDF.

## 📁 Project Structure

```
flyingsaucer/
├── src/
│   ├── main/
│   │   ├── java/com/example/flyingsaucer/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── model/          # Data models and entities
│   │   │   ├── repository/     # Database repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── config/         # Configuration classes
│   │   └── resources/
│   │       ├── static/images/  # Logo and static assets
│   │       ├── templates/      # Thymeleaf templates
│   │       └── application.properties
├── generated-pdfs/             # Output directory for PDFs
└── pom.xml                    # Maven configuration
```

## 🔧 Configuration

Key configuration options in `application.properties`:

```properties
# Server port
server.port=8080

# Template configuration
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML

# PDF output directory
pdf.output.dir=generated-pdfs/
```

## 🧪 Testing

Run the test suite with:
```bash
mvn test
```

Test the API endpoints using:
- Postman
- Swagger UI at http://localhost:8080/swagger-ui.html
- curl commands

## 📝 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 🤝 Contributing


1. Fork the project
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request


## 🚀 Deployment

To deploy this application:

1. Build the JAR file: `mvn clean package`
2. Run the JAR: `java -jar target/flyingsaucer-0.0.1-SNAPSHOT.jar`
3. For production, consider using:
   - Docker containerization
   - Cloud deployment (AWS, Azure, GCP)
   - Traditional server deployment
<img width="1830" height="839" alt="swagger" src="https://github.com/user-attachments/assets/f6ea082c-39c4-4b32-bf30-276651395e35" />

<img width="1898" height="921" alt="invoice" src="https://github.com/user-attachments/assets/31ea45a4-f200-4ea8-ae9b-eb2b77596851" />
