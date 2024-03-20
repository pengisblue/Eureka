import QRScanPage from '../pages/QRScanPage';
import ProductPage from '../pages/ProductPage/ProductPage';
import StatisticsPage from '../pages/StatisticsPage/StatisticsPage';
import { MaterialCommunityIcons } from '@expo/vector-icons';
import { createMaterialBottomTabNavigator } from '@react-navigation/material-bottom-tabs';
import HomeRouters from './HomeRouters';
import CardRouters from './CardRouters';


function Routers() {
  const Tab = createMaterialBottomTabNavigator()
  
  return (
    <Tab.Navigator 
      initialRouteName="HomePage"
      activeColor="#87CEEB"
      labelStyle={{ fontSize: 12 }}
      style={{ backgroundColor: '#87CEEB' }}
      >
      <Tab.Screen 
        name="HomePage" 
        component={HomeRouters} 
        options={{
          tabBarLabel: "홈",
          tabBarIcon: ({color}) => (
            <MaterialCommunityIcons name="home" color={color} size={26}/>
          )
        }}
        />
        <Tab.Screen 
        name="CardPage" 
        component={CardRouters} 
        options={{
          tabBarLabel: "카드",
          tabBarIcon: ({color}) => (
            <MaterialCommunityIcons name="credit-card" color={color} size={26}/>
          )
        }}
        />
        <Tab.Screen 
        name="QRScanPage" 
        component={QRScanPage} 
        options={{
          tabBarLabel: "QR결제",
          tabBarIcon: ({color}) => (
            <MaterialCommunityIcons name="qrcode" color={color} size={26}/>
          )
        }}
        />
        <Tab.Screen 
        name="StatisticsPage" 
        component={StatisticsPage} 
        options={{
          tabBarLabel: "통계",
          tabBarIcon: ({color}) => (
            <MaterialCommunityIcons name="chart-pie" color={color} size={26}/>
          )
        }}
        />
        <Tab.Screen 
        name="ProductPage" 
        component={ProductPage} 
        options={{
          tabBarLabel: "카드상품",
          tabBarIcon: ({color}) => (
            <MaterialCommunityIcons name="gift" color={color} size={26}/>
          )
        }}
        />
    </Tab.Navigator>
  );
}


export default Routers