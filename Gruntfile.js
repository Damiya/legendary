'use strict';

module.exports = function (grunt) {

  // Load grunt tasks automatically
  require('load-grunt-tasks')(grunt);

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  // Define the configuration for all the tasks
  grunt.initConfig({
    // Project settings
    project: {
      // configurable paths
      scala: 'app',
      app: 'public',
      dist: 'dist'
    },
    shell: {
      protractor: {
        options: {
          stdout: true
        },
        command: function (file) {
          var configFile = 'protractor.conf.js';
          var cmd = 'protractor ' + configFile;

          if (file && grunt.file.isFile(file)) {
            cmd += ' --specs ' + file;
          }

          console.log('Command: ' + cmd);
          return cmd;
        }
      }
    },
    'gh-pages': {
      options: {
        base: '<%= project.dist %>'
      },
      src: '**/*'
    },
    fileblocks: {
      dev: {
        src: '<%= project.app %>/index.html',
        options: {
          removeFiles: true
        },
        blocks: {
          'app': {
            src: 'scripts/**/*.js',
            cwd: '<%= project.app %>'
          }
        }
      },
      dist: {
        src: '<%= project.dist %>/index.html',
        options: {
          templates: {
            'css': '<link href="${file}" rel="stylesheet" />',
            'js': '<script src="${file}"></script>'
          },
          removeFiles: true
        },
        blocks: {
          'vendor': {
            src: 'scripts/vendor.js',
            cwd: '<%= project.dist %>'
          },
          'style': {
            src: 'styles/main.css',
            cwd: '<%= project.dist %>'
          },
          'app': {
            src: 'scripts/{legendary,templates}.js',
            cwd: '<%= project.dist %>'
          }

        }
      }
    },
    watch: {
      js: {
        files: ['<%= project.app %>/scripts/**/*.js'],
        tasks: ['fileblocks:dev'],
        options: {
          events: ['added', 'deleted']
        }
      },
//      jsTest: {
//        files: [
//          'test/e2e/**/*.spec.js',
//          'test/unit/**/*.spec.js'
//        ],
//        tasks: ['newer:jshint:test', 'karma']
//      },
      compass: {
        files: ['<%= project.app %>/styles/**/*.{scss,sass}'],
        tasks: ['compass:server', 'autoprefixer']
      },
      gruntfile: {
        files: ['Gruntfile.js']
      },
      livereload: {
        files: [
          '<%= project.app %>/views/**/*.{html,jade}',
          '<%= project.app %>/.tmp/styles/**/*.css',
          '<%= project.app %>/scripts/**/*.js',
          '<%= project.app %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}'
        ],

        options: {
          livereload: true
        }
      }
    },

    // Empties folders to start fresh
    clean: {
      dist: {
        options: {
          force: true
        },
        files: [
          {
            dot: true,
            src: [
              '.tmp',
              '<%= project.dist %>'
            ]
          }
        ]
      },
      'heroku-from-buildpack': {
        options: {
          force: true
        },
        files: [
          {
            dot: true,
            src: [
              '<%= project.app %>'
            ]
          }
        ]
      },
      server: '.tmp'
    },

    'bower-install': {
      app: {
        src: '<%= project.app %>/index.html',
        ignorePath: '<%= project.app %>/'
      }
    },

    // Add vendor prefixed styles
    autoprefixer: {
      options: {
        browsers: ['last 1 version']
      },
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= project.app %>/styles/',
            src: '**/*.css',
            dest: '<%= project.app %>/styles/'
          }
        ]
      }
    },

    // Compiles Sass to CSS and generates necessary files if requested
    compass: {
      options: {
        sassDir: '<%= project.app %>/styles',
        generatedImagesDir: '<%= project.app %>/images/generated',
        cssDir: '<%= project.app %>/.tmp/styles',
        imagesDir: '<%= project.app %>/.tmp/images',
        javascriptsDir: '<%= project.app %>/scripts',
        fontsDir: '<%= project.app %>/styles/fonts',
        importPath: '<%= project.app %>/bower_components',
        httpImagesPath: '/images',
        httpGeneratedImagesPath: '/images/generated',
        httpFontsPath: '/styles/fonts',
        relativeAssets: false,
        assetCacheBuster: false,
        raw: 'Sass::Script::Number.precision = 10\n'
      },
      dist: {
        options: {
          generatedImagesDir: '<%= project.dist %>/images/generated'
        }
      },
      server: {
        options: {
          debugInfo: true
        }
      }
    },

    // Renames files for browser caching purposes
    rev: {
      dist: {
        files: {
          src: [
            '<%= project.dist %>/scripts/**/*.js',
            '<%= project.dist %>/styles/**/*.css',
            //'<%= project.dist %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
            '<%= project.dist %>/styles/fonts/*'
          ]
        }
      }
    },

    // Reads HTML for usemin blocks to enable smart builds that automatically
    // concat, minify and revision files. Creates configurations in memory so
    // additional tasks can operate on them
    useminPrepare: {
      html: ['<%= project.app %>/index.html'],
      options: {
        dest: '<%= project.dist %>'
      }
    },

    // Performs rewrites based on rev and the useminPrepare configuration
    usemin: {
      html: ['<%= project.dist %>/views/**/*.html',
        '<%= project.dist %>/index.html'],
      css: ['<%= project.dist %>/styles/**/*.css'],
      options: {
        basedir: '<%= project.dist %>',
        dirs: '<%= project.dist %>'
      }
    },

    // The following *-min tasks produce minified files in the dist folder
    imagemin: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= project.app %>/images',
            src: '**/*.{png,jpg,jpeg,gif}',
            dest: '<%= project.dist %>/images'
          }
        ]
      }
    },

    svgmin: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= project.app %>/images',
            src: '**/*.svg',
            dest: '<%= project.dist %>/images'
          }
        ]
      }
    },

    htmlmin: {
      dist: {
        options: {
          collapseBooleanAttributes: true,
          collapseWhitespace: true,
          removeAttributeQuotes: true,
          removeComments: true, // Only if you don't use comment directives!
          removeEmptyAttributes: true,
          removeRedundantAttributes: true,
          removeScriptTypeAttributes: true,
          removeStyleLinkTypeAttributes: true
        },
        files: [
          {
            expand: true,
            cwd: '<%= project.dist %>',
            src: ['views/**/*.html'],
            dest: '<%= project.dist %>'
          },
          {
            expand: true,
            cwd: '<%= project.dist %>',
            src: ['index.html'],
            dest: '<%= project.dist %>'
          }

        ]
      }
    },

    // Allow the use of non-minsafe AngularJS files. Automatically makes it
    // minsafe compatible so Uglify does not destroy the ng references
    ngmin: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= project.dist %>/scripts',
            src: '*.js',
            dest: '<%= project.dist %>/scripts'
          }
        ]
      }
    },

    ngtemplates: {
      legendary: {
        cwd: 'public',
        src: ['template/**/*.html', 'views/**/*.html'],
        dest: 'dist/scripts/templates.js',
        options: {
          htmlmin: '<%= htmlmin.dist %>'
        }
      }
    },

    // Replace Google CDN references
    cdnify: {
      dist: {
        html: ['<%= project.dist %>/*.html']
      }
    },

    // Moves files to dist directory
    copy: {
      dist: {
        files: [
          {
            expand: true,
            dot: true,
            cwd: '<%= project.app %>',
            dest: '<%= project.dist %>',
            src: [
              '*.{ico,png,txt}',
              '.htaccess',
              'bower_components/**/*',
              'images/**/*.{webp}',
              'fonts/**/*',
              'views/**/*.html',
              'index.html'
            ]
          },
          {
            expand: true,
            cwd: '.tmp/concat',
            dest: '<%= project.dist %>',
            src: '**/*'
          },
          {
            expand: true,
            cwd: '.tmp/images',
            dest: '<%= project.dist %>/images',
            src: ['generated/*']
          }
        ]
      },
      'heroku-from-buildpack': {
        files: [
          {
            expand: true,
            dot: true,
            cwd: '<%= project.dist %>',
            dest: '<%= project.app %>',
            src: [
              '**/*.*'
            ]
          }
        ]
      }
    },

    // Run some tasks in parallel to speed up the build process
    concurrent: {
      dev: [
        'compass:server',
        'fileblocks:dev'
      ],
      test: [
        'compass'
      ],
      dist: [
        'imagemin',
        'svgmin',
        'htmlmin'
      ]
    },

//
//    cssmin: {
//      dist: {
//        files: {
//          '<%= project.dist %>/styles/main.css': [
//            '.tmp/styles/**/*.css',
//            '<%= project.app %>/styles/**/*.css'
//          ]
//        }
//      }
//    },
//    uglify: {
//      dist: {
//        files: {
//          '<%= project.dist %>/scripts/legendary.js': [
//            '<%= project.dist %>/scripts/legendary.js'
//          ],
//          '<%= project.dist %>/scripts/vendor.js': [
//            '<%= project.dist %>/scripts/vendor.js'
//          ]
//        }
//      }
//    },
//    concat: {
//      dist: {
//        options: {
//          stripBanners: true
//        },
//        files: {
//          '<%= project.dist %>/scripts/legendary.js': ['public/scripts/**/*.js'],
//          '<%= project.dist %>/scripts/vendor.js': ['public/bower_components/**/*.js']
//        }
//      }
//    },


    // Test settings
    karma: {
      unit: {
        configFile: 'karma.conf.js',
        singleRun: true
      }
    }
  });

  grunt.registerTask('dev', [
    'clean:server',
    'concurrent:dev',
    'autoprefixer',
    'watch'
  ]);

  grunt.registerTask('test', [
    'clean:server',
    'concurrent:test',
    'autoprefixer',
    'karma',
    'shell:protractor'
  ]);

  grunt.registerTask('build', [
    'clean:dist',
    'useminPrepare',
    'autoprefixer',
    'compass:dist',
    'concat',
    'copy:dist',
    'ngtemplates:legendary',
    'fileblocks:dist',
    'concurrent:dist',
    'ngmin',
    'cssmin',
    'uglify',
    'usemin'
  ]);

  grunt.registerTask('heroku-from-buildpack', 'Execute a build sequence in the heroku buildpack', function () {
    grunt.task.run([
      'build',
      'clean:heroku-from-buildpack',
      'copy:heroku-from-buildpack',
      'clean:dist'
    ]);
  });

  grunt.registerTask('default', [
    'newer:jshint',
    'test',
    'build'
  ]);
};
