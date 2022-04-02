<?php

namespace CamCam\Handler;

class Get
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function handle()
    {
        $db = $this->db;

        $sql = "SELECT `data` FROM `observations`";

        $result = $db->execute($sql);

        $data = [];

        while ($row = $result->fetch_assoc()) {
            $data[] = json_decode($row['data']);
        }

        header("Content-Type: application/json");

        print(json_encode([
            "data" => $data
        ]));
    }
}
