<?php
$mysql_host     = "127.0.0.1";
$mysql_username = "username";
$mysql_password = "password";
$mysql_database = "tutorial";

$redis_host = "127.0.0.1";
$redis_port = 6379;
$redis_pass = "";

$sql   = "SELECT * FROM planet";
$mysql = new mysqli($mysql_host, $mysql_username, $mysql_password, $mysql_database);
if ($mysql->connect_error) {
  die("Connection failed: " . $mysql->connect_error);
}

$redis = new Redis();
try {
  $redis->connect($redis_host, $redis_port);
  //$redis->auth($redis_pass);
}catch (RedisException $ex) {
  echo "Redis connection error!";
  return false;
}


//$redis->del($sql);
if ($redis->exists($sql)) 
{
  $result = $redis->get($sql);
  echo "From Cache<br>";
  echo $result;
} 
else 
{
  if ($query = $mysql->query($sql)) {
    $result = $query->fetch_all(MYSQLI_ASSOC);
    $result = json_encode($result);

    echo "From Database<br>";
    echo $result;
    
    $redis->setex($sql, 10, $result);
    $query->free();
  }
}

$redis->close();
$mysql->close();
?>