<?php
/*
* bd type:
*   1 - крестики
*   0 - нолики
*   -1 - игра в ходе поиска
*   -2 - в процессе игры
* move type:
    1 - крестики
    0 - нолики
*/

ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

$type = $_GET['type'];

switch ($type) {
    case 'create_room':
        echo create_game();
        break;
    case 'join_room':
        $id = $_GET['id'];
        echo join_game($id);
        break;
    case 'create_action':
        move($_GET['id'], $_GET['x'], $_GET['y'], $_GET['type_move']);
        break;
    case 'long_get':
        echo long_get($_GET['id']);
        break;
    case 'wait_game':
        echo wait_game($_GET['id']);
        break;
    case 'get_games':
        get_games();
}

function get_games(){
    $db = new SQLite3('bd');
    $results = $db->query('SELECT * FROM Games WHERE type=-1');
    while ($row = $results->fetchArray()) {
        echo $row['id'];
        echo '`';
    }
    $db->close();
}
function wait_game($id) {
    while(true){
        $db = new SQLite3('bd');
        $results = $db->query('SELECT * FROM Games WHERE id='. $id);
        while ($row = $results->fetchArray()) {
            if($row['type'] == '-2'){
                $movety = $row['move_type'];
                $db->close();
                return $movety;
            }
            $db->close();
        }
        sleep(1);
    }
}
function long_get($id){
    $db = new SQLite3('bd');
  
    $results = $db->query('SELECT * FROM GamesInfo WHERE id='. $id);
    $count = 0;
    while ($row = $results->fetchArray()) {
        $count++;
    }
    $db->close();

    while(true){
        $new_count = 0;
        $db = new SQLite3('bd');
  
        $results = $db->query('SELECT * FROM GamesInfo WHERE id='. $id);
        $x = 0;
        $y = 0;
        while ($row = $results->fetchArray()) {
            $new_count++;
            $x = $row['x'];
            $y = $row['y'];
        }

        $db->close();

        if($new_count != $count){
            return $x.";".$y;
        }

        sleep(1);
    }
}
function move($id, $x, $y, $type){

    $db = new SQLite3('bd');
    $db->query('INSERT INTO GamesInfo
    (id, x, y, "type")
    VALUES('.$id.', '.$x.', '.$y.', '.$type.'); ');
    $db->close();

}

function get_my_move_type($id){
    $db = new SQLite3('bd');
  
    $results = $db->query('SELECT * FROM Games WHERE id='. $id);
    while ($row = $results->fetchArray()) {
        if($row['move_type'] == '1'){
            $db->close();
            return 0;
        }
        $db->close();
        return 1;
    }
}
function join_game($id){

    $db = new SQLite3('bd');
    $db->exec('UPDATE Games
    SET "type"=-2
    WHERE id='.$id.';');
    $db->close();
    return get_my_move_type($id);
}
function create_game(){  
    $id = time();
    $db = new SQLite3('bd');
    $db->exec('INSERT INTO Games
    (id, "type", move_type)
    VALUES('.$id.', -1, '.rand(0, 1).');');
    $db->close();
    return $id;
}
?>