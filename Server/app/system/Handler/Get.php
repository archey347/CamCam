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
        print("Get");
    }
}
