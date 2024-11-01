import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/home/HomeView.vue'
import HomePage from "@/views/home/components/HomePage.vue";
import EventPage from "@/views/home/components/EventPage.vue";
import ControlView from "@/views/control/ControlView.vue";
import UserPage from "@/views/control/components/UserPage.vue";
import ControlPage from "@/views/control/components/ControlPage.vue";
import LevelPage from "@/views/home/components/LevelPage.vue";
import LoginView from "@/views/login/LoginView.vue";
import EventControlPage from "@/views/control/components/EventControlPage.vue";
import ReviewPage from "@/views/control/components/ReviewPage.vue";
import ChatPage from "@/views/home/components/ChatPage.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/home',
            name: 'home',
            component: HomeView,
            children: [{
                path: '',
                name: 'homePage',
                component: HomePage
            },
                {
                    path: '/event',
                    name: 'eventPage',
                    component: EventPage
                }, {
                    path: '/level',
                    name: 'levelPage',
                    component: LevelPage
                }, {
                    path: '/chat',
                    name: 'chat',
                    component: ChatPage
                }]
        },
        {
            path: '/',
            name: 'login',
            component: LoginView
        },
        {
            path: '/control',
            name: 'control',
            component: ControlView,
            children: [
                {
                    path: '',
                    name: 'controlPage',
                    component: ControlPage,
                },
                {
                    path: '/control/user',
                    name: 'controlUserPage',
                    component: UserPage
                },
                {
                    path: '/control/event',
                    name: 'eventControlPage',
                    component: EventControlPage
                },
                {
                    path: '/control/review',
                    name: 'reviewControlPage',
                    component: ReviewPage
                }
            ]
        }
    ]
})

export default router
