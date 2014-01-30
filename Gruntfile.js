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
        fileblocks: {
            netbeans: {
                src: '<%= project.app %>/views/index.html',
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
            scala: {
                src: '<%= project.scala %>/views/index.scala.html',
                options: {
                    templates: {
                        js: '<script src=\'@routes.Assets.at("${file}")\'></script>'
                    },
                    removeFiles: true
                },
                blocks: {
                    'app': {
                        src: 'scripts/**/*.js',
                        cwd: '<%= project.app %>'
                    }
                }
            }
        },
        watch: {
            js: {
                files: ['<%= project.app %>/scripts/**/*.js']
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
                    '<%= project.scala %>/views/**/*.scala.html',
                    '<%= project.app %>/views/**/*.{html,jade}',
                    '<%= project.app %>/styles/**/*.css',
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
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp',
                            '<%= yeoman.dist %>/views/*',
                            '<%= yeoman.dist %>/public/*',
                            '!<%= yeoman.dist %>/public/.git*'
                        ]
                    }
                ]
            },
            server: '.tmp'
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
                        cwd: '<%= project.app%>/styles/',
                        src: '**/*.css',
                        dest: '<%= project.app%>/styles/'
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
                    generatedImagesDir: '<%= yeoman.dist %>/public/images/generated'
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
                        '<%= yeoman.dist %>/public/scripts/**/*.js',
                        '<%= yeoman.dist %>/public/styles/**/*.css',
                        '<%= yeoman.dist %>/public/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
                        '<%= yeoman.dist %>/public/styles/fonts/*'
                    ]
                }
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: ['<%= project.app %>/views/index.html',
                '<%= project.app %>/views/index.jade'],
            options: {
                dest: '<%= yeoman.dist %>/public'
            }
        },

        // Performs rewrites based on rev and the useminPrepare configuration
        usemin: {
            html: ['<%= yeoman.dist %>/views/**/*.html',
                '<%= yeoman.dist %>/views/**/*.jade'],
            css: ['<%= yeoman.dist %>/styles/**/*.css'],
            options: {
                assetsDirs: ['<%= yeoman.dist %>/public']
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
                        dest: '<%= yeoman.dist %>/public/images'
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
                        dest: '<%= yeoman.dist %>/public/images'
                    }
                ]
            }
        },

        htmlmin: {
            dist: {
                options: {
                    //collapseWhitespace: true,
                    //collapseBooleanAttributes: true,
                    //removeCommentsFromCDATA: true,
                    //removeOptionalTags: true
                },
                files: [
                    {
                        expand: true,
                        cwd: '<%= project.app %>/views',
                        src: ['*.html', 'partials/*.html'],
                        dest: '<%= yeoman.dist %>/views'
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
                        cwd: '.tmp/concat/scripts',
                        src: '*.js',
                        dest: '.tmp/concat/scripts'
                    }
                ]
            }
        },

        // Replace Google CDN references
        cdnify: {
            dist: {
                html: ['<%= yeoman.dist %>/views/*.html']
            }
        },

        // Copies remaining files to places other tasks can use
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= project.app %>',
                        dest: '<%= yeoman.dist %>/public',
                        src: [
                            '*.{ico,png,txt}',
                            '.htaccess',
                            'bower_components/**/*',
                            'images/**/*.{webp}',
                            'fonts/**/*'
                        ]
                    },
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= project.app %>/views',
                        dest: '<%= yeoman.dist %>/views',
                        src: '**/*.jade'
                    },
                    {
                        expand: true,
                        cwd: '.tmp/images',
                        dest: '<%= yeoman.dist %>/public/images',
                        src: ['generated/*']
                    },
                    {
                        expand: true,
                        dest: '<%= yeoman.dist %>',
                        src: [
                            'package.json',
                            'server.js',
                            'lib/**/*'
                        ]
                    }
                ]
            },
            styles: {
                expand: true,
                cwd: '<%= project.app %>/styles',
                dest: '.tmp/styles/',
                src: '**/*.css'
            }
        },

        // Run some tasks in parallel to speed up the build process
        concurrent: {
            scala: [
                'compass:server',
                'fileblocks:scala'
            ],
            netbeans: [
                'compass:server',
                'fileblocks:netbeans'
            ],
            test: [
                'compass'
            ],
            dist: [
                'compass:dist',
                'imagemin',
                'svgmin',
                'htmlmin'
            ]
        },

        // By default, your `index.html`'s <!-- Usemin block --> will take care of
        // minification. These next options are pre-configured if you do not wish
        // to use the Usemin blocks.
        // cssmin: {
        //   dist: {
        //     files: {
        //       '<%= yeoman.dist %>/styles/main.css': [
        //         '.tmp/styles/**/*.css',
        //         '<%= project.app %>/styles/**/*.css'
        //       ]
        //     }
        //   }
        // },
        // uglify: {
        //   dist: {
        //     files: {
        //       '<%= yeoman.dist %>/scripts/scripts.js': [
        //         '<%= yeoman.dist %>/scripts/scripts.js'
        //       ]
        //     }
        //   }
        // },
        // concat: {
        //   dist: {}
        // },

        // Test settings
        karma: {
            unit: {
                configFile: 'karma.conf.js',
                singleRun: true
            }
        }
    });

    grunt.registerTask('express-keepalive', 'Keep grunt running', function () {
        this.async();
    });

    grunt.registerTask('scala', [
        'clean:server',
        'concurrent:scala',
        'autoprefixer',
        'watch'
    ]);


    grunt.registerTask('netbeans', [
        'clean:server',
        'concurrent:netbeans',
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
        'concurrent:dist',
        'autoprefixer',
        'concat',
        'ngmin',
        'copy:dist',
        'cdnify',
        'cssmin',
        'uglify',
        'rev',
        'usemin'
    ]);


    grunt.registerTask('default', [
        'newer:jshint',
        'test',
        'build'
    ]);
};
