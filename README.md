   # JabberPoint

   A Java-based presentation tool with DTAP workflow.

   ## Development Process (DTAP)

   This project follows a DTAP (Development, Testing, Acceptance, Production) workflow:

   ### Branches
   - `main`: Production branch
   - `acceptance`: Stakeholder review branch
   - `test`: Testing branch
   - `development`: Active development branch

   ### Workflow
   1. Create feature branches from `development`
   2. Merge completed features into `development`
   3. Promote code from `development` to `test` for QA
   4. Promote code from `test` to `acceptance` for stakeholder review
   5. Promote code from `acceptance` to `main` for production deployment

   ### Running the Application
   ```bash
   mvn clean package
   java -jar target/jabberpoint-1.0-SNAPSHOT.jar
   ```

   ### Running Tests
   ```bash
   mvn test
   ```

   ### Viewing Code Coverage
   After running tests, open `target/site/jacoco/index.html` in a browser.