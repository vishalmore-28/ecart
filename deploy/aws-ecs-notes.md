# AWS production layout (ECR + ECS Fargate + RDS + ALB)

1. **RDS**: PostgreSQL 16 (db.t4g.micro) or Oracle. Private subnets; security group allows the DB port only from the ECS tasks.
   The app creates its own tables on first start.
2. **ECR**: build and push the image
   ```bash
   aws ecr create-repository --repository-name ecart
   aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin <acct>.dkr.ecr.ap-south-1.amazonaws.com
   docker build -t ecart .
   docker tag ecart:latest <acct>.dkr.ecr.ap-south-1.amazonaws.com/ecart:latest
   docker push <acct>.dkr.ecr.ap-south-1.amazonaws.com/ecart:latest
   ```
3. **Secrets Manager**: store `DB_PASSWORD` and `ADMIN_PASSWORD`; reference them in the task definition `secrets` block.
4. **ECS Fargate service**: 1 task (0.5 vCPU / 1 GB), container port 8080, environment
   `DB_URL=jdbc:postgresql://<rds-endpoint>:5432/ecart` (or `jdbc:oracle:thin:@//<rds-endpoint>:1521/ORCL`), `DB_USER`, `ADMIN_EMAIL`.
5. **ALB** (HTTPS 443 + ACM certificate) -> target group port 8080, health check `/login`. Enable sticky sessions (sessions live in Tomcat memory) or run one task.
6. After HTTPS works, add `<secure>true</secure>` inside `<cookie-config>` in `WEB-INF/web.xml`.
