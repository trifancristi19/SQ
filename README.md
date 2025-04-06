# JabberPoint 🎯

A modern Java-based presentation tool following DTAP workflow principles and SOLID design patterns.

## 🚀 Features
- Java-based presentation creation and viewing
- XML-based presentation format
- DTAP workflow integration
- Comprehensive test coverage
- Modern Swing-based UI
- SOLID principles implementation

## 👥 Contributors
- Cristian Trifan
- Mihail Josan

## 🏗️ Architecture & Design

### SOLID Principles
This project adheres to the SOLID principles of object-oriented design:

- **Single Responsibility Principle (SRP)**: Each class has a single, well-defined responsibility
- **Open/Closed Principle (OCP)**: Classes are open for extension but closed for modification
- **Liskov Substitution Principle (LSP)**: Subtypes can be substituted for their base types
- **Interface Segregation Principle (ISP)**: Interfaces are specific to client needs
- **Dependency Inversion Principle (DIP)**: High-level modules don't depend on low-level modules

## 🔄 Development Process (DTAP)

This project follows a DTAP (Development, Testing, Acceptance, Production) workflow:

### 🌿 Branches
- `main`: Production branch
- `acceptance`: Stakeholder review branch
- `test`: Testing branch
- `development`: Active development branch

### 📋 Workflow
1. Create feature branches from `development`
2. Merge completed features into `development`
3. Promote code from `development` to `test` for QA
4. Promote code from `test` to `acceptance` for stakeholder review
5. Promote code from `acceptance` to `main` for production deployment

## 🛠️ Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6.0 or higher

### Installation
```bash
# Clone the repository
git clone [repository-url]

# Navigate to project directory
cd jabberpoint
```

### Building the Application
```bash
mvn clean package
```

### Running the Application
```bash
java -jar target/jabberpoint-1.0-SNAPSHOT.jar
```

### Running Tests
```bash
mvn test
```

### Viewing Code Coverage
After running tests, open `target/site/jacoco/index.html` in your browser to view the test coverage report.

## 📝 License
