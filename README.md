# ecart 2.0 - Online Electronic Store (Servlet + JSP + JDBC)

Runs on **Oracle** (local) **or PostgreSQL** (Neon / Render) - the database is chosen only by environment variables,
exactly like your first version. The same WAR works on both.

## 1. Environment variables
| Variable | Meaning |
|---|---|
| `DB_URL` | Oracle: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` &nbsp;|&nbsp; PostgreSQL/Neon: `jdbc:postgresql://HOST/DBNAME?sslmode=require` |
| `DB_USER` | database user |
| `DB_PASSWORD` | its password (`DB_PASS` also works) |
| `DB_DRIVER` | optional - picked automatically from `DB_URL` (you can leave your old value, it still works) |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | the first admin account, created on first start (default `admin@ecart.local` / `Admin@123` - **set your own**) |

Oracle service name (`lsnrctl status`): `FREEPDB1` = 23ai Free, `XEPDB1` = XE 18c/21c, `XE` = XE 11g, `ORCL` = 19c.
Your old default `.../FREE` is the 23ai Free *container root*; user tables normally live in `FREEPDB1`. Use whichever your old app used successfully.

The Oracle driver (`ojdbc17.jar`, the same file your old project used) is already inside `src/main/webapp/WEB-INF/lib`, so Maven does not need to download it.

## 2. Tables: old and new live side by side
The new app creates its own tables on first start: `users, categories, products, cart_items, wishlist, addresses, coupons,
shop_orders, order_items, order_events, reviews, audit_log`. They never touch your old `PRODUCT, CUSTOMER, ADMIN, ORDERS`,
so you can use the **same database and the same user**, and rollback is instant (put the old WAR back).
Oracle: the user needs `CREATE TABLE` and `CREATE SEQUENCE` (`GRANT CREATE SEQUENCE TO <user>;` if you get ORA-01031).

## 3. Upgrade your GitHub repo (github.com/vishalmore-28/ecart)
```bash
git clone https://github.com/vishalmore-28/ecart.git && cd ecart
git checkout -b v2-upgrade
git rm -r main ecart.war Dockerfile          # old source layout + old WAR + old Dockerfile
# unzip ecart-pro-v2.zip and copy EVERYTHING inside the ecart-pro-v2 folder into this repo folder
git add -A
git commit -m "ecart 2.0: Maven project, new UI, cart/checkout/orders/admin, Oracle + PostgreSQL"
git push -u origin v2-upgrade
```
(`README.md` and the old screenshots can stay; replace them later with new screenshots.)
Test the branch first (step 4 or 5). When happy: merge `v2-upgrade` into `main` on GitHub.

## 4. Run locally on Oracle (Eclipse)
1. Eclipse: File > Import > Maven > Existing Maven Projects > this folder. Needs **JDK 17 or newer (JDK 25 is fine)**, the latest **Tomcat 9.0.x** (not 10) and, for JDK 25, Eclipse 2025-09 or newer.
2. Run > Run Configurations > Apache Tomcat > your server > **Environment** tab: add `DB_URL`, `DB_USER`, `DB_PASSWORD`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`.
3. Run As > Run on Server. Open `http://localhost:8080/ecart/`. The Tomcat console must show **`ecart: database ready (Oracle)`**.
4. Admin: `/admin` (the e-mail/password you set).

## 5. Deploy on Render + Neon (your current live setup)
1. In Render > your web service > **Environment**: keep `DB_URL`, `DB_USER`, `DB_PASSWORD` (your Neon values) and ADD `ADMIN_EMAIL`, `ADMIN_PASSWORD`. `DB_DRIVER` can stay or be deleted.
2. The service is Docker-based: Render now builds from the new `Dockerfile` (it compiles the WAR itself - you no longer commit `ecart.war`).
   Point the service at branch `v2-upgrade` to test, then at `main`.
3. Deploy. Check the Render log for **`ecart: database ready (PostgreSQL)`**. First load after a build can take a minute.

## 6. Import your OLD customers, products and orders (one time)
Do this after the new app started once (so the new tables exist), using the SAME `DB_*` variables:
```bash
mvn -q compile exec:java -Dexec.args=dry      # preview only, writes nothing
mvn -q compile exec:java                      # real import
```
(Eclipse alternative: open `com.ecart.tools.MigrateOldData`, Run As > Java Application, same Environment variables; add program argument `dry` for the preview.)
For Neon, run it on your laptop with Neon's `DB_URL`/user/password. What it does: products -> new catalogue (category "Imported"), customers -> new accounts with
their **old password hash (old passwords keep working)**, old orders -> order history (status DELIVERED). Old tables are only read.
**Customers now log in with their e-mail address** (a customer with an invalid/missing e-mail gets `username@imported.local`).
Customers whose password was not hashed are skipped and listed. Your old admin account is not copied - the new admin comes from `ADMIN_EMAIL`/`ADMIN_PASSWORD`.
Safe to repeat: existing items are skipped and orders are imported only once.

## 7. Docker (optional)
`cp .env.example .env` (edit), then `docker compose --env-file .env up --build` (PostgreSQL) or
`docker compose -f docker-compose.oracle.yml --env-file .env up --build` (Oracle XE). Open http://localhost:8080 .
AWS: `deploy/aws-ec2.sh` (single server) or `deploy/aws-ecs-notes.md` (ECS + RDS).

## 8. Troubleshooting
| Message | Fix |
|---|---|
| `ORA-12514` / `ORA-12505` | wrong service name in `DB_URL` (see section 1) |
| `ORA-01017` | wrong user/password |
| `ORA-01031` / `ORA-01950` | `GRANT CREATE SEQUENCE, CREATE TABLE TO <user>;` and give quota on the tablespace |
| `ClassNotFoundException: oracle.jdbc.OracleDriver` | check that `src/main/webapp/WEB-INF/lib/ojdbc17.jar` exists, then Server > Clean |
| PostgreSQL `permission denied for schema public` | use the Neon owner user (the one your old app used) |
| 404 on every page | Tomcat 10 - use Tomcat 9 |
| `Unsupported class file major version` when opening a page | your Tomcat 9 is too old for your JDK - download the latest 9.0.x |
| blank page / 500 | read the Tomcat or Render log: the line `ecart: DATABASE INIT FAILED - ...` says why |

## 9. Features
Customer: register/login, search + filters + sort + paging, product page, reviews, wishlist, cart, coupons (`WELCOME10`, `FESTIVE15`, `SAVE5`),
address book, checkout (COD / UPI / card - **demo gateway, no real money**), GST + shipping, order tracking, cancel before shipping, profile.
Admin: dashboard, product CRUD, orders with status flow, customer enable/disable.
Security: PBKDF2 hashes, CSRF tokens, prepared statements, XSS escaping, session regeneration, login lockout, CSP headers, ownership checks.
