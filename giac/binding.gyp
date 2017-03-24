{
  "targets": [
    {
      "target_name": "giac",
      "sources" : [  "<!@(node -p \"require('fs').readdirSync('./src/giac/cpp/').map(f=>'src/giac/cpp/'+f).join(' ')\")",
          "src/nodegiac/cpp/nodegiac.cc" ],
      "include_dirs": ['src/giac/headers'],
      "defines" : [
          "GIAC_GGB",
          "IN_GIAC",
          "GIAC_GENERIC_CONSTANTS",
          "HAVE_UNISTD_H",
          "HAVE_LIBPTHREAD",
          "HAVE_SYSCONF",
          "HAVE_NO_HOME_DIRECTORY",
          'VERSION="1.2.3"',
          "TIMEOUT",
          'HAVE_SYS_TIMES_H',
          'HAVE_SYS_TIME_H',
      ],
      "cflags_cc" : [
          "-fexceptions", "-fpermissive"
      ],
      "cflags_cc!" : [
          "-fno-rtti"
      ],
      "link_settings" : {
        "libraries" : [
          "-lgmp"
        ]
      # See the bigint package how to do this linking on different operating systems.
      }
    }
  ]
}
