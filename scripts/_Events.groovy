eventInitScm = { basedir, interactive ->
    scmProvider = classLoader.loadClass("grails.plugin.git.GitScmProvider").newInstance(basedir, interactive)
}

