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
        print("Post");
    }
}
