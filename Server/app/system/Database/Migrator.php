<?php

namespace CamCam\Database;

class Migrator
{
    public $db;

    public const OBSERVATIONS_SQL = <<<SQL

        CREATE TABLE IF NOT EXISTS `observations` (
            `id` int NOT NULL,
            `data` text NOT NULL
        );
    SQL;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function migrate()
    {
        $this->db->execute(self::OBSERVATIONS_SQL);
    }
}
