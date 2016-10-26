<?php

/**
 * Copyright ©2010 Taggart Spilman
 * tagadvance@gmail.com
 */
error_reporting ( E_ALL | E_STRICT );
ini_set ( 'display_errors', '1' );

$fileSeparator = '/';
$path = explode ( $fileSeparator, $_SERVER ['SCRIPT_NAME'] );
$name = array_pop ( $path );
$ignore = array (
		'.',
		'..',
		$name 
);

$files = getDirFiles ( '.', $ignore );
if ($files !== false) {
	$uri = $_SERVER ['SCRIPT_URI'];
	$url = explode ( $fileSeparator, $uri );
	array_pop ( $url );
	foreach ( $files as $k => &$file ) {
		if (! isImageFile ( $file )) {
			unset ( $files [$k] );
			continue;
		}
		$copy = $url;
		$copy [] = $file;
		$file = implode ( $fileSeparator, $copy );
	}
	print implode ( "\n", $files );
}

function getDirFiles($dir = '.', $ignore = array('.', '..')) {
	if (! is_array ( $ignore )) {
		// throw error
	}
	$cwd = array ();
	if (is_dir ( $dir ) && $handle = opendir ( $dir )) {
		while ( ($file = readdir ( $handle )) !== false ) {
			if (! in_array ( $file, $ignore ) && ! is_dir ( $file )) {
				$cwd [] = $file;
			}
		}
		closedir ( $handle );
	} else
		return false;
	return $cwd;
}

function isImageFile($filename) {
	static $exts = array (
			'.jpg',
			'.jpeg',
			'.jpe',
			'.jif',
			'.jfif',
			'.jfi',
			'.gif',
			'.png',
			'.bmp',
			'.dib',
			'.tif',
			'.tiff' 
	);
	foreach ( $exts as $ext ) {
		if (endsWith ( $filename, $ext )) {
			return true;
		}
	}
	return false;
}

function endsWith($str, $sub) {
	return (substr ( $str, strlen ( $str ) - strlen ( $sub ) ) === $sub);
}

?>
