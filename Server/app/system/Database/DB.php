<?php

namespace CamCam\Database;

class DB
{
    public $db_host;
    public $db_user;
    public $db_pass;
    public $db_name;

    private $db;

    public function __construct()
    {
        $this->db_host = $_ENV['DB_HOST'];
        $this->db_user = $_ENV['DB_USER'];
        $this->db_pass = $_ENV['DB_PASS'];
        $this->db_name = $_ENV['DB_NAME'];
    }

    public function connect()
    {
        $this->db = new \mysqli($this->db_host, $this->db_user, $this->db_pass, $this->db_name);

        if ($this->db->connect_errno) {
            echo "Failed to connect to MySQL: (" . $this->db->connect_errno . ") " . $this->db->connect_error;
            http_response_code(500);
            die();
        }
    }

    public function execute($sql)
    {
        if (!$this->db) {
            $this->connect();
        }

        $result = $this->db->query($sql);

        if (!$result) {
            echo "Query failed: (" . $this->db->errno . ") " . $this->db->error;
            http_response_code(500);
            die();
        }

        return $result;
    }

    public function escape_string($string)
    {
        if (!$this->db) {
            $this->connect();
        }

        return $this->db->real_escape_string($string);
    }
}
