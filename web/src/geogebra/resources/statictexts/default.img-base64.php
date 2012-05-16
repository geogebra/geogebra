<?
# This PHP script returns a base64 encoded version of the default.img.
# Parameter: date, example date=20120505
# @author Zoltan Kovacs <zoltan@geogebra.org>
$date=$_GET["date"];
header("Content-Type: text/plain");
header("Content-Disposition: attachment; filename=\"default.img-$date-base64.txt\"");
ob_clean();
flush();
echo base64_encode(file_get_contents("./default.img-$date"));
exit;
?>
