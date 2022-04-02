<?php

namespace CamCam;

class Auth
{
    public function check()
    {
        if (!isset($_GET['key'])) {
            return false;
        }

        print(getenv('API_KEY'));

        if ($_GET['key'] != $_ENV['API_KEY']) {
            return false;
        }

        return true;
    }
}
