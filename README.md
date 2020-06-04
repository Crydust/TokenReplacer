TokenReplacer
=============

[![DepShield Badge](https://depshield.sonatype.org/badges/Crydust/TokenReplacer/depshield.svg)](https://depshield.github.io)

A simple replacement for ants replace with replacetoken.

    usage: java -jar tokenreplacer.jar
     -b,--begintoken <token>     begintoken (default @)
     -D <key=value>              key value pairs to replace (required unless
                                 replacetokens file is defined)
     -e,--endtoken <token>       endtoken (default @)
     -f,--folder <folder>        folder (default current directory)
     -h,--help                   print this message
     -q,--quiet                  quiet mode, do not ask if ok to replace
     -r,--replacetokens <file>   property file containing key value pairs (use
                                 -D to override)
     -x,--exclude <glob>         glob pattern to exclude
