package no.amumurst
package server

import cats.effect.IO
import services.HighGarden

object Main extends HighGarden[IO]
