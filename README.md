# 🏦 Banking_Project

A web-based banking application built with **Java**, **Apache Tomcat**, and **MySQL**.  
This guide explains how to install, configure, and run the project.

---

## 📌 Requirements
- Java JDK 8 or higher
- Apache Tomcat (tested on Tomcat)
- MySQL Server
- `Banking_Project.war` file
- `backupbank.sql` file

---

## 🚀 Deploy Project on Tomcat
1. Navigate to the Tomcat directory → `webapps`
2. Copy the `Banking_Project.war` file into the `webapps` folder
3. Start Tomcat:
   ```bash
   cd /path/to/tomcat/bin
   ./startup.sh
   ```
4. Open a browser and visit:
   ```
   http://localhost:8080/Banking_Project/
   ```

---

## 🗄️ Setup Database
1. Download the `backupbank.sql` file
2. Open a terminal and log in to MySQL:
   ```bash
   mysql -u root -p
   ```
   *(Example password: `Imageinfo@123`)*

3. Create and use the database:
   ```sql
   CREATE DATABASE bank;
   USE bank;
   ```

4. Import the SQL file:
   ```sql
   SOURCE /home/administrator/Downloads/backupbank.sql;
   (Go to the folder where your project and the backup SQL file are located.
     For example, if your project is in “Downloads/backupbank.sql”, navigate to that folder.
   ```

---

## ▶️ Run the Application
1. Ensure Tomcat is running
2. Open your browser and go to:
   ```
   http://localhost:8080/Banking_Project/
   ```

✅ The Banking Project is now installed and ready to use.

---

## ⚙️ Change Database Credentials (if needed)
1. Open the `Banking_Project.war` file
2. Navigate to: `WEB-INF/classes/db.properties`
3. Update `username` and `password` as required
4. Save changes 
5. If a popup appears → click **Update**
6. Database credentials are now updated

---

## 🔑 Test Credentials
### Admin Login
- **Admin ID:** `1` 
- **Username:** `admin` 
- **Password:** `admin123`

### User Login
- **Customer ID:** `1008` 
- **Password:** `1234`

---

## 👤 How to Use the Project

### New User Flow
1. Go to **Accounts Section → Account Opening**
   - Fill in the required fields and submit 
   - Credentials will be sent to your registered email 
2. **Sign Up** your account
   - Create your password and log in 
3. **Create MPIN** (mandatory for secure transactions) 
4. **Debit Card** is created automatically 
5. Set your **Debit Card PIN** 
6. To pay by Debit Card: 
   - Go to **Pay by Card** option 
   - Fill the form and submit 
7. To get a **Credit Card**: 
   - Apply for it 
   - Admin must approve → then it will be generated 
8. For **UPI Transactions**: 
   - Create your UPI ID 
   - Fill details 
   - QR code is auto-generated 
   - You can also scan other users’ QR codes for payment 
9. For **Loans**: 
   - Go to respective Loan section and apply 
   - Once confirmed, loan amount will be disbursed 
   - You can track your loan status 
10. To check **Transaction History**: 
    - Go to Transaction Section 
    - Select date range 
    - View or download your transactions 
    - Your Account Number is your password to open transaction history PDF
11. To check **Balance**: 
    - Use the Balance Inquiry option

---

### Admin Section
- View and manage: 
  - Credit Card requests 
  - Loan requests 
- Approve or reject requests 
- Deposit money into accounts 

---

## 📌 Notes
- Tested only on **Apache Tomcat**
- Ensure MySQL is running before starting the app
- Update database credentials in `db.properties` if using custom values

---

🎉 **Installation & Usage Complete!** 
Your Banking Project is up and running.
