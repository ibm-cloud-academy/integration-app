# Integration app

This repository stores the application for the integration portion of CK104 course.

Instruction for using this repo:

1. Clone the repo `git clone https://github.com/ibm-cloud-academy/integration-app`

2. Go to the repo `cd integration-app`

3. Create ear file `./gradlew build`

4. Copy the ear file `cp FrontEnd/build/libs/FrontEnd.ear server1/apps`

5. Push application to IBM Cloud `ibmcloud app push <route> -p server1`

Modify the necessary files as indicated by lab guide and redo the step 3/4/5 ...

