<?php
// Need this to get defn of DEBUG_FC
require_once("fc_db.php");

if(DEBUG_FC==1)
{
	require_once("auth_net_test.php");
}
else
{
	require_once("auth_net_live.php");
}
?>