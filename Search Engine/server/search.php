<?php
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$mode = isset($_REQUEST['algo']) ? $_REQUEST['algo'] : "default";
$results = false;
if ($query)
{
 // The Apache Solr Client library should be on the include path
 // which is usually most easily accomplished by placing in the
 // same directory as this script ( . or current directory is a default
 // php include path entry in the php.ini)
 require_once('Apache/Solr/Service.php');
 // create a new solr service instance - host, port, and corename
 // path (all defaults in this example)
 $solr = new Apache_Solr_Service('localhost', 8983, '/solr/mycore2/');
 // if magic quotes is enabled then stripslashes will be needed
 if (get_magic_quotes_gpc() == 1)
 {
 $query = stripslashes($query);
 }
 // in production code you'll always want to use a try /catch for any
 // possible exceptions emitted by searching (i.e. connection
 // problems or a query parsing error)
 try
 {
     if ($mode == "pageRank") {
        $additionalParameters = array(
            // 'fl' => 'id',
            'sort' => 'pageRankFile desc'
           );
        $results = $solr->search($query, 0, $limit, $additionalParameters);
     }
     else {
        $results = $solr->search($query, 0, $limit);
     }
 }
 catch (Exception $e)
 {
 // in production you'd probably log or email this error to an admin
 // and then show a special message to the user but for this example
 // we're going to show the full exception
 die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
 }}
?>
<html>
 <head>
 <title>PHP Solr Client Example</title>
 <style>
   ul {
       list-style: none;
       /*border: 1px solid black;*/
       width: 200px;
   }

   #datalist li {
       border: 1px solid black;
   }
 </style>
 </head>
 <body>
 <form accept-charset="utf-8" method="get">
 <label for="q">Search:</label>
 <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" onkeyup="suggest(this.value)" autocomplete="off" />
 <div id="datalist"></div>
 <input name="algo" type="radio" value="default" <?php if ($mode == "default") echo 'checked="checked"'; ?> /><label>Lucene</label>
 <input name="algo" type="radio" value="pageRank" <?php if ($mode == "pageRank") echo 'checked="checked"'; ?> /><label>PageRank</label>
 <input type="submit" value="Submit"/>
 </form>
<?php
// display results
if ($results)
{
 $total = (int) $results->response->numFound;
 $start = min(1, $total);
 $end = min($limit, $total);
?>
 <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
 <ol>
<?php
  if ($total == 0) {
    $corrector = 'http://localhost/HW4/corrector.php?q='.urlencode($query);
    $correct = file_get_contents($corrector); 
    echo '<p>Did you mean <a href="'.'http://localhost/HW4/search.php?q='.$correct.'&algo='.$mode.'">'.$correct.'</a>?</p>';
}
 // iterate result documents
 foreach ($results->response->docs as $doc)
 {
?>
 <li>
 <table style="border: 1px solid black; text-align: left;width: 1000px;">
<?php
 // iterate document fields / values
 $id = "N/A";
 $title = "N/A";
 $url = "N/A";
 $desc = "N/A";
 foreach ($doc as $field => $value)
 {
     if ($field == "id") {
         $id = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
     }
     elseif ($field == "title") {
         $title = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
     }
     elseif ($field == "og_url") {
         $url = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
     }
     elseif ($field == "og_description") {
         $desc = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
     }
}

// searching in csv if og_url is not valid
if ($query && $url == "N/A") {
    $filename = explode('\\', trim($id));
    $csv = fopen("URLtoHTML_nytimes_news.csv", "r");
    while(!feof($csv)) {
        $line = fgets($csv, 4096);
        $line_seg = explode(",", trim($line));
        if ($filename[count($filename) - 1] == $line_seg[0]) {
            $url = $line_seg[1];
            break;
        }
    }
    fclose($csv);
}

?>
 <tr>
 <th><?php echo "ID: " ?></th>
 <td><?php echo $id?></td>
 </tr>
 <tr>
 <th><?php echo "Title: " ?></th>
 <td><?php echo '<a href="'.$url.'">'.$title.'</a>';?></td>
 </tr>
 <tr>
 <th><?php echo "URL: " ?></th>
 <td><?php echo '<a href="'.$url.'">'.$url.'</a>';?></td>
 </tr>
 <tr>
 <th><?php echo "Desc: " ?></th>
 <td><?php echo $desc?></td>
 </tr>
 </table>
 </li>
<?php
 }
?>
 </ol>
<?php
}
?>
 </body>
 <script>
    function choose(li) {
        var input = document.getElementById("q");
        input.value = li.innerHTML;
        document.getElementById("datalist").innerHTML = "";
    }

    function over(li) {
        li.style.background = "yellow";
    }

    function out(li) {
        li.style.background = "white";
    }

    function suggest(e) {
        var xmlhttp = new XMLHttpRequest();
        
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                var obj = JSON.parse(xmlhttp.responseText);
                var tmp = obj.suggest.suggest;
                var arr = null;
                for (var p in tmp) {
                    arr = tmp[p].suggestions;
                }
                if (arr) {
                    var datalist = "<ul>";
                    for (var i = 0; i < arr.length; i++) {
                        datalist += '<li onclick="choose(this)" onmouseover="over(this)" onmouseout="out(this)">' + arr[i].term + "</li>";
                    }
                    datalist += "</ul>";
                    document.getElementById("datalist").innerHTML = datalist;
                }
            }
        }

        xmlhttp.open("GET", "http://localhost/HW4/suggest.php?q=" + e);
        xmlhttp.send();
    }
 </script>
</html>