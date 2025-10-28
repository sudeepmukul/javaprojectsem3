CREATE TABLE department (
  dept_id    NUMBER PRIMARY KEY,
  dept_name  VARCHAR2(50) NOT NULL,
  location   VARCHAR2(50)
);
CREATE TABLE employee (
  emp_id      NUMBER PRIMARY KEY,
  first_name  VARCHAR2(50),
  last_name   VARCHAR2(50),
  dept_id     NUMBER REFERENCES department(dept_id),
  job_title   VARCHAR2(50),
  salary      NUMBER(10,2),
  hire_date   DATE DEFAULT SYSDATE,
  status      VARCHAR2(20) DEFAULT 'ACTIVE',
  terminated_on DATE
);
CREATE TABLE performance (
  perf_id     NUMBER PRIMARY KEY,
  emp_id      NUMBER REFERENCES employee(emp_id),
  rating      NUMBER(2),
  review_date DATE DEFAULT SYSDATE,
  remarks     VARCHAR2(200)
);
CREATE TABLE salary_audit (
  audit_id    NUMBER PRIMARY KEY,
  emp_id      NUMBER,
  old_salary  NUMBER(10,2),
  new_salary  NUMBER(10,2),
  change_date DATE DEFAULT SYSDATE
);
CREATE OR REPLACE TRIGGER trg_salary_audit
BEFORE UPDATE OF salary ON employee
FOR EACH ROW
DECLARE
  v_new_id NUMBER;
BEGIN
  SELECT NVL(MAX(audit_id), 0) + 1 INTO v_new_id FROM salary_audit;

  IF NVL(:OLD.salary, 0) <> NVL(:NEW.salary, 0) THEN
    INSERT INTO salary_audit (audit_id, emp_id, old_salary, new_salary, change_date)
    VALUES (v_new_id, :OLD.emp_id, :OLD.salary, :NEW.salary, SYSDATE);
  END IF;
END;
/


CREATE OR REPLACE PACKAGE pkg_manage AS
  PROCEDURE hire_emp(p_empid NUMBER, p_fname VARCHAR2, p_lname VARCHAR2, 
                     p_dept NUMBER, p_job VARCHAR2, p_salary NUMBER);
  PROCEDURE fire_emp(p_empid NUMBER);
END pkg_manage;
/

----------------------------
CREATE OR REPLACE PACKAGE BODY pkg_manage AS

  PROCEDURE hire_emp(p_empid NUMBER, p_fname VARCHAR2, p_lname VARCHAR2, 
                     p_dept NUMBER, p_job VARCHAR2, p_salary NUMBER) IS
  BEGIN
    INSERT INTO employee(emp_id, first_name, last_name, dept_id, job_title, salary, hire_date, status)
    VALUES (p_empid, p_fname, p_lname, p_dept, p_job, p_salary, SYSDATE, 'ACTIVE');
    COMMIT;
  END;

  PROCEDURE fire_emp(p_empid NUMBER) IS
  BEGIN
    UPDATE employee 
    SET status = 'TERMINATED', terminated_on = SYSDATE
    WHERE emp_id = p_empid;
    COMMIT;
  END;

END pkg_manage;
------------------------
INSERT INTO department VALUES (1, 'HR', 'Mumbai');
INSERT INTO department VALUES (2, 'Engineering', 'Bangalore');
INSERT INTO department VALUES (3, 'Sales', 'Delhi');

INSERT INTO employee VALUES (100, 'Amit', 'Kumar', 2, 'Developer', 40000, TO_DATE('2024-05-10','YYYY-MM-DD'), 'ACTIVE', NULL);
INSERT INTO employee VALUES (101, 'Neha', 'Sharma', 1, 'HR Executive', 35000, TO_DATE('2023-11-21','YYYY-MM-DD'), 'ACTIVE', NULL);
INSERT INTO employee VALUES (102, 'Rahul', 'Singh', 3, 'Sales Manager', 50000, TO_DATE('2024-08-05','YYYY-MM-DD'), 'ACTIVE', NULL);

INSERT INTO performance VALUES (1000, 100, 5, TO_DATE('2024-12-01','YYYY-MM-DD'), 'Excellent work');
INSERT INTO performance VALUES (1001, 101, 4, TO_DATE('2024-11-15','YYYY-MM-DD'), 'Good communication');

SELECT * FROM EMPLOYEE;
BEGIN
  pkg_manage.hire_emp(107, 'Prem', 'Nair', 2, 'Tester', 42000);
END;
BEGIN
  pkg_manage.fire_emp(102);
END;
/
SELECT * FROM EMPLOYEE;
UPDATE employee SET salary = 65000 WHERE emp_id = 100;
UPDATE employee SET salary = 65000 WHERE emp_id = 100;
COMMIT;
SELECT * FROM salary_audit;
---------------------------------------------------------------------------------------
SELECT e.emp_id, e.first_name || ' ' || e.last_name AS emp_name,
       d.dept_name, e.job_title, e.salary, e.status
FROM employee e
JOIN department d ON e.dept_id = d.dept_id
ORDER BY e.emp_id;
-----------------------------------------

-- 1. Display all employees
SELECT * FROM employee;

-- 2. List employees with their department names
SELECT e.emp_id, e.first_name || ' ' || e.last_name AS emp_name,
       d.dept_name
FROM employee e
JOIN department d ON e.dept_id = d.dept_id;

-- 3. Show employees with their current salary
SELECT emp_id, first_name || ' ' || last_name AS emp_name, salary
FROM employee
ORDER BY salary DESC;

-- 4. Find total salary expense per department
SELECT d.dept_name, SUM(e.salary) AS total_salary
FROM employee e
JOIN department d ON e.dept_id = d.dept_id
GROUP BY d.dept_name;

-- 5. List terminated employees
SELECT emp_id, first_name || ' ' || last_name AS emp_name, status, terminated_on
FROM employee
WHERE status = 'TERMINATED';

-- 6. Count employees in each department
SELECT d.dept_name, COUNT(e.emp_id) AS total_employees
FROM department d
LEFT JOIN employee e ON d.dept_id = e.dept_id
GROUP BY d.dept_name;

-- 7. Display all performance ratings
SELECT e.first_name || ' ' || e.last_name AS emp_name,
       p.rating, p.review_date, p.remarks
FROM performance p
JOIN employee e ON p.emp_id = e.emp_id
ORDER BY p.review_date DESC;

-- 8. Show salary changes logged in salary_audit
SELECT s.audit_id, e.first_name || ' ' || e.last_name AS emp_name,
       s.old_salary, s.new_salary, s.change_date
FROM salary_audit s
JOIN employee e ON s.emp_id = e.emp_id
ORDER BY s.change_date DESC;

-- 9. Show top 3 highest-paid active employees
SELECT e.emp_id, e.first_name || ' ' || e.last_name AS emp_name,
       e.salary, d.dept_name
FROM employee e
JOIN department d ON e.dept_id = d.dept_id
WHERE e.status = 'ACTIVE'
ORDER BY e.salary DESC
FETCH FIRST 3 ROWS ONLY;

-- 10. List employees with average performance rating >= 4
SELECT e.emp_id, e.first_name || ' ' || e.last_name AS emp_name,
       AVG(p.rating) AS avg_rating
FROM employee e
JOIN performance p ON e.emp_id = p.emp_id
GROUP BY e.emp_id, e.first_name, e.last_name
HAVING AVG(p.rating) >= 4
ORDER BY avg_rating DESC;
