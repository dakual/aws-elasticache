<?php

$redis = new Redis();
$redis->connect('127.0.0.1', 6379);
//$redis->auth('REDIS_PASSWORD');

$key = "SELECT * FROM planet";

if (!$redis->get($key)) {
    $source = 'MySQL Server';

    $mysql_host     = '127.0.0.1';
    $mysql_database = 'tutorial';
    $mysql_username = 'username';
    $mysql_password = 'password';

    $pdo = new PDO('mysql:host=' . $mysql_host . '; dbname=' . $mysql_database, $mysql_username, $mysql_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->prepare($key);
    $stmt->execute();

    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
       $products[] = $row;
    }

    $redis->set($key, serialize($products));
    $redis->expire($key, 10);

} else {
     $source = 'Redis Server';
     $products = unserialize($redis->get($key));

}

echo $source . ': <br>';
print_r($products);