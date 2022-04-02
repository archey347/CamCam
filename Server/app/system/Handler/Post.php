<?php

namespace CamCam\Handler;

class Post
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function handle()
    {
        $data = $_POST;
        $db = $this->db;

        if (!isset($data['data'])) {
            print("Invalid request");
            http_response_code(400);
            exit;
        }

        $data = $db->escape_string($data['data']);

        $sql = "INSERT INTO `observations` (`data`) VALUES ('$data')";

        $result = $db->execute($sql);

        print("OK");
    }
}
