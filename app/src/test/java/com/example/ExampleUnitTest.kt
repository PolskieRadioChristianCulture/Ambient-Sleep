package com.example

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun generateLauncherIcons() {
    // Locate res directory
    var resDir = File("src/main/res")
    if (!resDir.exists()) {
        resDir = File("app/src/main/res")
    }
    if (!resDir.exists()) {
        resDir = File("../app/src/main/res")
    }
    assertTrue("Resource directory should exist!", resDir.exists())

    println("Fuzzy scanning for user-uploaded assets...")
    val possibleRootDirs = listOf(
        File("."),
        File(".."),
        File("/")
    )
    
    var foundLogoFile: File? = null
    var foundBannerFile: File? = null
    
    // 1. Search for specific files first (exact match)
    for (dir in possibleRootDirs) {
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles() ?: emptyArray()
            for (f in files) {
                if (f.isFile && f.length() > 0) {
                    val name = f.name
                    if (name == "grafika apki Ambient  (2).jpg") {
                        foundBannerFile = f
                    }
                    if (name == "Logo ambient  sleep.jpg" && foundLogoFile == null) {
                        foundLogoFile = f
                    }
                }
            }
        }
    }
    
    // 2. Fallbacks for banner
    if (foundBannerFile == null) {
        for (dir in possibleRootDirs) {
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles() ?: emptyArray()
                for (f in files) {
                    if (f.isFile && f.length() > 0) {
                        val name = f.name
                        if (name == "grafika apki Ambient .jpg") {
                            foundBannerFile = f
                            break
                        }
                    }
                }
                if (foundBannerFile != null) break
            }
        }
    }
    if (foundBannerFile == null) {
        for (dir in possibleRootDirs) {
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles() ?: emptyArray()
                for (f in files) {
                    if (f.isFile && f.length() > 0) {
                        val lower = f.name.lowercase()
                        if (lower.contains("grafika") && lower.endsWith(".jpg")) {
                            foundBannerFile = f
                            break
                        }
                    }
                }
                if (foundBannerFile != null) break
            }
        }
    }
    
    // 3. Fallbacks for logo
    if (foundLogoFile == null) {
        for (dir in possibleRootDirs) {
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles() ?: emptyArray()
                for (f in files) {
                    if (f.isFile && f.length() > 0) {
                        val name = f.name
                        if (name == "Ambient Sleep.jpg") {
                            foundLogoFile = f
                            break
                        }
                    }
                }
                if (foundLogoFile != null) break
            }
        }
    }
    if (foundLogoFile == null) {
        for (dir in possibleRootDirs) {
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles() ?: emptyArray()
                for (f in files) {
                    if (f.isFile && f.length() > 0) {
                        val lower = f.name.lowercase()
                        if (lower.contains("logo") && lower.endsWith(".jpg") && !lower.contains("original")) {
                            foundLogoFile = f
                            break
                        }
                    }
                }
                if (foundLogoFile != null) break
            }
        }
    }

    // 1. Copy Banner if found
    if (foundBannerFile != null) {
        val destBanner = File(resDir, "drawable/ambient_sleep_hero.jpg")
        println("Copying user-uploaded hero banner from ${foundBannerFile.absolutePath} to ${destBanner.absolutePath}")
        foundBannerFile.copyTo(destBanner, overwrite = true)
    } else {
        println("No non-empty user banner file was found.")
    }

    // 2. Copy Logo if found
    val srcFile = File(resDir, "drawable/logo_ambient_sleep.jpg")
    if (foundLogoFile != null) {
        println("Copying user-uploaded logo from ${foundLogoFile.absolutePath} to ${srcFile.absolutePath}")
        foundLogoFile.copyTo(srcFile, overwrite = true)
    } else {
        println("No user logo file was found. Proceeding with existing logo.")
    }
    
    assertTrue("Source logo image must exist at ${srcFile.absolutePath}", srcFile.exists())
    println("Source file size in bytes: ${srcFile.length()}")

    val img = ImageIO.read(srcFile)
    assertNotNull("Loaded image must not be null!", img)
    println("Loaded image width: ${img.width}, height: ${img.height}")

    val densities = mapOf(
        "mdpi" to 48,
        "hdpi" to 72,
        "xhdpi" to 96,
        "xxhdpi" to 144,
        "xxxhdpi" to 192
    )

    for ((density, size) in densities) {
        val dir = File(resDir, "mipmap-$density")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        // 1. Generate ic_launcher_ambient.png
        val outSquare = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g2 = outSquare.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2.drawImage(img, 0, 0, size, size, null)
        g2.dispose()
        val squareFile = File(dir, "ic_launcher_ambient.png")
        ImageIO.write(outSquare, "png", squareFile)
        assertTrue(squareFile.exists())

        // 2. Generate ic_launcher_ambient_round.png
        val outCircle = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val gc = outCircle.createGraphics()
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        gc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        // Draw opaque black disk
        gc.color = Color.BLACK
        gc.fillOval(0, 0, size, size)

        // Elliptical clip to exact circle bounds
        val clipShape = Ellipse2D.Double(0.0, 0.0, size.toDouble(), size.toDouble())
        gc.clip = clipShape
        gc.drawImage(img, 0, 0, size, size, null)
        gc.dispose()
        val circleFile = File(dir, "ic_launcher_ambient_round.png")
        ImageIO.write(outCircle, "png", circleFile)
        assertTrue(circleFile.exists())

        // 3. Generate ic_launcher_ambient_foreground.png (with safe-zone scaling & transparency blend)
        val fgSize = when (density) {
            "mdpi" -> 108
            "hdpi" -> 162
            "xhdpi" -> 216
            "xxhdpi" -> 324
            "xxxhdpi" -> 432
            else -> 192
        }
        val outFgRaw = BufferedImage(fgSize, fgSize, BufferedImage.TYPE_INT_ARGB)
        val gfg = outFgRaw.createGraphics()
        gfg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        
        // Scale and draw centered to avoid clipping in adaptive safe zone (~76% size)
        val scaleFactor = 0.76
        val scaledSize = (fgSize * scaleFactor).toInt()
        val offset = (fgSize - scaledSize) / 2
        gfg.drawImage(img, offset, offset, scaledSize, scaledSize, null)
        gfg.dispose()

        // Process pixels to make dark background transparent
        val outFg = BufferedImage(fgSize, fgSize, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until fgSize) {
            for (x in 0 until fgSize) {
                val argb = outFgRaw.getRGB(x, y)
                val alpha = (argb ushr 24) and 0xFF
                if (alpha == 0) {
                    outFg.setRGB(x, y, 0)
                    continue
                }
                val r = (argb ushr 16) and 0xFF
                val g = (argb ushr 8) and 0xFF
                val b = argb and 0xFF
                
                val maxVal = maxOf(r, g, b)
                // Threshold for black pixels: if max color component is less than 45, make transparent / semi-transparent
                if (maxVal < 45) {
                    val factor = maxVal / 45.0
                    val newAlpha = (alpha * factor).toInt()
                    // Re-apply alpha
                    val newArgb = (newAlpha shl 24) or (r shl 16) or (g shl 8) or b
                    outFg.setRGB(x, y, newArgb)
                } else {
                    outFg.setRGB(x, y, argb)
                }
            }
        }
        val fgFile = File(dir, "ic_launcher_ambient_foreground.png")
        ImageIO.write(outFg, "png", fgFile)
        assertTrue(fgFile.exists())
    }
    println("Launcher icons with ic_launcher_ambient prefix generated successfully.")
  }
}

