<?php

namespace CamCam;

use Dotenv\Dotenv;
use CamCam\Database\Migrator;
use CamCam\Database\DB;

class App
{
    public $config;
    public $db;

    public function __construct()
    {
        $dotenv = Dotenv::createImmutable(__DIR__);
        $dotenv->load();

        $this->config = $dotenv;
        $this->db = new DB();
    }

    public function install()
    {
        $migrator = new Migrator($this->db);
        $migrator->migrate();
    }

    public function run()
    {

        // Check auth
        $auth = new Auth();

        if (!$auth->check()) {
            print("Unauthorized");
            http_response_code(401);
            exit;
        }

        $action = $_SERVER['REQUEST_METHOD'];

        if ($action == 'POST') {
            $handler = new Handler\Post($this->db);

            $handler->handle();
        } elseif ($action == 'GET') {
            $handler = new Handler\Get($this->db);

            $handler->handle();
        } else {
            print("Invalid request");
            http_response_code(400);
            exit;
        }
    }
}
