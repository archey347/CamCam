<?php

namespace CamCam;

use Dotenv\Dotenv;

class App
{
    public $config;

    public function __construct()
    {
        $dotenv = Dotenv::createImmutable(__DIR__);
        $dotenv->load();

        $this->config = $dotenv;
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

        print("Hello World!");
    }
}
