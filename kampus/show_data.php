<?php
	$DB_NAME = "kampus";
	$DB_USER = "root";
	$DB_PASS = "";
	$DB_SERVER_LOC = "localhost";

	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		$conn = mysqli_connect($DB_SERVER_LOC,$DB_USER,$DB_PASS,$DB_NAME);
		$sql = "SELECT m.nim, m.nama, p.nama_prodi, concat('http://localhost/kampus/images/',photos) as url
				FROM mahasiswa m, prodi p 
				WHERE m.id_prodi=p.id_prodi ";
		$result = mysqli_query($conn,$sql);
		if(mysqli_num_rows($result)>0){
			header("Access-Control-Allow-Origin: *");
			header("Content-type: application/json; chatset=UTF-8");

			$data_mhs = array();
			while($mhs = mysqli_fetch_assoc($result)){
				array_push($data_mhs, $mhs);
			}
			echo json_encode($data_mhs);
		}
	}
?>