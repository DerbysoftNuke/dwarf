import org.apache.commons.io.IOUtils

import java.util.concurrent.TimeUnit

TimeUnit.MILLISECONDS.sleep(500)
return '************'+a+'************\n'+IOUtils.toString(new URL("http://13.228.106.181/dwarf/status.ci"), "UTF-8")