<?php
ini_set('memory_limit', -1);

include 'SpellCorrector.php';

$wrong = $_GET['q'];
echo SpellCorrector::correct($wrong);
//it will output *october*
?>