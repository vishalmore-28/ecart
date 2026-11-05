-- ============================================================================
-- OPTIONAL: only if you want a brand-new Oracle user for ecart 2.0.
-- You can also simply keep using the SAME user as your old app: the new tables have different names
-- (the new orders table is SHOP_ORDERS, so it never clashes with the old ORDERS table).
-- Run as SYSTEM, connected to the pluggable database:
--   Oracle 23ai Free : sqlplus system/<pwd>@localhost:1521/FREEPDB1
--   XE 18c/21c       : sqlplus system/<pwd>@localhost:1521/XEPDB1
--   XE 11g           : sqlplus system/<pwd>@localhost:1521/XE
--   19c              : sqlplus system/<pwd>@localhost:1521/ORCL
-- ============================================================================
CREATE USER ecart IDENTIFIED BY ecart123
  DEFAULT TABLESPACE users
  QUOTA UNLIMITED ON users;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO ecart;

-- If you keep your OLD user instead, it only needs the sequence right (tables it already has):
-- GRANT CREATE SEQUENCE TO <your_old_user>;
