
set(GeoGebraThumbnail_SRCS geogebracreator.cpp)

#qt4_add_resources(GeoGebraThumbnail_SRCS geogebrathumbnail.qrc)

kde4_add_plugin(geogebrathumbnail ${GeoGebraThumbnail_SRCS})

target_link_libraries(geogebrathumbnail ${KDE4_KIO_LIBS} )

install(TARGETS geogebrathumbnail DESTINATION ${PLUGIN_INSTALL_DIR} )

install(FILES  geogebrathumbnail.desktop DESTINATION ${SERVICES_INSTALL_DIR})
