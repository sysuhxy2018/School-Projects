<?php
$keyword = $_GET['q'];
$url = 'http://localhost:8983/solr/mycore2/suggest?q='.urlencode($keyword);
echo file_get_contents($url);
?>