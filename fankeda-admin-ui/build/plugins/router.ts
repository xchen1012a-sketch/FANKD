import type { RouteMeta } from 'vue-router';
import ElegantVueRouter from '@elegant-router/vue/vite';
import type { RouteKey } from '@elegant-router/types';

const routeMetaMap: Partial<Record<RouteKey, Partial<RouteMeta>>> = {
  home: {
    icon: 'mdi:monitor-dashboard',
    order: 1
  },
  stalls: {
    icon: 'mdi:storefront-outline',
    order: 2
  },
  queues: {
    icon: 'mdi:account-group-outline',
    order: 3
  },
  recommend: {
    icon: 'mdi:chart-timeline-variant',
    order: 4
  },
  feedback: {
    icon: 'mdi:message-text-outline',
    order: 5
  },
  users: {
    icon: 'mdi:account-multiple-outline',
    order: 6
  },
  settings: {
    icon: 'mdi:cog-outline',
    order: 7
  }
};

export function setupElegantRouter() {
  return ElegantVueRouter({
    layouts: {
      base: 'src/layouts/base-layout/index.vue',
      blank: 'src/layouts/blank-layout/index.vue'
    },
    routePathTransformer(routeName, routePath) {
      const key = routeName as RouteKey;

      if (key === 'login') {
        const modules: UnionKey.LoginModule[] = ['pwd-login', 'code-login', 'register', 'reset-pwd', 'bind-wechat'];

        const moduleReg = modules.join('|');

        return `/login/:module(${moduleReg})?`;
      }

      return routePath;
    },
    onRouteMetaGen(routeName) {
      const key = routeName as RouteKey;

      const constantRoutes: RouteKey[] = ['login', '403', '404', '500'];

      const meta: Partial<RouteMeta> = {
        title: key,
        i18nKey: `route.${key}` as App.I18n.I18nKey
      };

      Object.assign(meta, routeMetaMap[key]);

      if (constantRoutes.includes(key)) {
        meta.constant = true;
      }

      return meta;
    }
  });
}
