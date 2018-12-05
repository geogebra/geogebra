{
  "targets": [
    {
      "target_name": "giac",
      "sources" : [  "<!@(node -p \"require('fs').readdirSync('./src/giac/cpp/').map(f=>'src/giac/cpp/'+f).join(' ')\")",
          "src/nodegiac/cpp/nodegiac.cc" ],
      "include_dirs": ['src/giac/headers'],
      # Common defines:
      "defines" : [
        "GIAC_GGB",
        'VERSION="1.2.3"',
        "IN_GIAC",
        "HAVE_SYSCONF",
        "HAVE_NO_HOME_DIRECTORY",
        "TIMEOUT",
        'HAVE_MPFR_1',
        'HAVE_LIBMPFR'
        ],
      'conditions': [
        ['OS=="linux"',
          {
            "cflags_cc" : [
              "-fexceptions", "-fpermissive"
              ],
            "cflags_cc!" : [
              "-fno-rtti"
              ],
            'link_settings': {
              'libraries': [
                '-lgmp', '-lmpfr'
              ]
            },
            "defines+" : [ "HAVE_LIBPTHREAD" ]
          }
        ],
        ['OS!="win"',
          {
            "defines+" : [
              "GIAC_GENERIC_CONSTANTS",
              "HAVE_UNISTD_H",
              'HAVE_SYS_TIMES_H',
              'HAVE_SYS_TIME_H',
              ],
          }
        ],
        ['OS=="mac"',
          {
            'defines+': [
              'NO_STDEXCEPT',
              'APPLE_SMART',
              'NO_GETTEXT',
              'CLANG' ],
            'link_settings': {
              'libraries': [
                '-lgmp', '-lmpfr'
              ]
            },
            'xcode_settings': { 'GCC_ENABLE_CPP_RTTI': 'YES',
            'OTHER_CPLUSPLUSFLAGS' : ['-std=c++11', '-stdlib=libc++', "-Wno-narrowing", "-fexceptions"],
            'OTHER_LDFLAGS' : ['-L/opt/local/lib', # Assuming MacPorts is used to provide libgmp.
              "-L$(LIBDIR)/." ] # But user defined LIBDIR is also allowed. (FIXME: this will use "/." if LIBDIR is empty.)
            }
          }
        ],
        ['OS=="win"',
          {
            'conditions': [
              ['target_arch=="x64"', {
                'defines+': [
                  'x86_64'
                  ]
                }
              ]
            ],
            'defines+': [
              '__VISUALC__',
              'HAVE_NO_SYS_TIMES_H',
              'HAVE_NO_PWD_H',
              'HAVE_NO_SYS_RESOURCE_WAIT_H',
              'HAVE_NO_CWD',
              'MS_SMART'
            ],
            'link_settings': {
              'libraries': [
                '-lmpir.lib', '-lmpfr.lib'
              ]
            },
            "configurations": {
                "Release": {
                    "msvs_settings": {
                      'VCCLCompilerTool': {
                            'RuntimeTypeInfo': 'true',
                            'ExceptionHandling': 1, # /EHsc # seems to have no effect
                        },
                      "VCLinkerTool": {
                            "AdditionalLibraryDirectories": [
                                ".", "..", "$(LIBDIR)" ]
                        }
                    }
                },
                # This is a bit ugly. We repeat the same settings here as for "Release".
                "Debug": {
                    "msvs_settings": {
                      'VCCLCompilerTool': {
                            'RuntimeTypeInfo': 'true',
                            'ExceptionHandling': 1, # /EHsc # seems to have no effect
                        },
                        "VCLinkerTool": {
                            "AdditionalLibraryDirectories": [
                                ".", "..", "$(LIBDIR)" ]
                        }
                    }
                }
            }
          }
        ]
      ]
    }
  ]
}
