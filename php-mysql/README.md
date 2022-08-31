### Debian and Ubuntu Php Install
```sh
sudo apt-get install php-cli
sudo apt-get install php-redis
sudo apt-get install php-mysql
```

### Docker Php
```sh
docker run --rm \
    -u $(id -u):$(id -g) \
    -v $(pwd):/app \
    -p 8080:80 \
    --name php \
    php:8.1.5-cli-alpine3.15 php -S 0.0.0.0:80 -t /app
```

### Start
```sh
php -S 127.0.0.1:8000
```