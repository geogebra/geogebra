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
      'conditions': [
        ['OS=="linux"',
          {
            'link_settings': {
              'libraries': [
                '-lgmp'
              ]
            }
          }
        ],
        ['OS=="mac"',
          {
            'defines+': [ 
              'NO_STDEXCEPT',
              'APPLE_SMART',
              'HAVE_UNISTD_H',
              'NO_GETTEXT',
              'CLANG' ],
            'link_settings': {
              'libraries': [
                '-lgmp'
              ]
            },
            'xcode_settings': { 'GCC_ENABLE_CPP_RTTI': 'YES',
            'OTHER_CPLUSPLUSFLAGS' : ['-std=c++11', '-stdlib=libc++', "-Wno-narrowing", "-fexceptions"],
            'OTHER_LDFLAGS' : ['-L/opt/local/lib'] } # Assuming MacPorts is used to provide libgmp.
          }
        ],
        ['OS=="win"',
          {
            'link_settings': {
              'libraries': [
                '-lgmp.lib'
              ],
            }
          }
        ]
      ]
    }
  ]
}
