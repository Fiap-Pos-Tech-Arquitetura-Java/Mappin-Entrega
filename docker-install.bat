docker pull postgres:latest
docker run --name mappin-standalone-entrega-db -p 5435:5435 -e PGPORT=5435 -e POSTGRES_USER=mappin -e POSTGRES_PASSWORD=mappinEntrega -e POSTGRES_DB=mappin-entrega-db -d postgres